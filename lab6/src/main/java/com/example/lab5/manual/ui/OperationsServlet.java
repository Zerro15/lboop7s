package com.example.lab5.manual.ui;

import com.example.lab5.manual.config.FactoryHolder;
import com.example.lab5.manual.functions.operations.TabulatedDifferentialOperator;
import com.example.lab5.manual.functions.operations.TabulatedFunctionOperationService;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;
import com.example.lab5.manual.functions.tabulated.TabulatedFunctionMapper;
import com.example.lab5.manual.functions.tabulated.TabulatedPoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Сервлет с поэлементными операциями и дифференцированием.
 */
@WebServlet("/ui/api/operations/*")
public class OperationsServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getPathInfo();
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            TabulatedFunctionOperationService ops = new TabulatedFunctionOperationService(FactoryHolder.getInstance().getFactory());
            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator(FactoryHolder.getInstance().getFactory());

            if (path != null && path.contains("derivative")) {
                List<TabulatedPoint> sourcePoints = objectMapper.convertValue(payload.get("function"), new TypeReference<List<TabulatedPoint>>() {});
                TabulatedFunction source = TabulatedFunctionMapper.fromPoints(sourcePoints, FactoryHolder.getInstance().getFactory());
                TabulatedFunction result = differentialOperator.differentiate(source);
                writeFunction(resp, result);
                return;
            }

            String operation = payload.get("operation").asText();
            List<TabulatedPoint> firstPoints = objectMapper.convertValue(payload.get("first"), new TypeReference<List<TabulatedPoint>>() {});
            List<TabulatedPoint> secondPoints = objectMapper.convertValue(payload.get("second"), new TypeReference<List<TabulatedPoint>>() {});

            TabulatedFunction first = TabulatedFunctionMapper.fromPoints(firstPoints, FactoryHolder.getInstance().getFactory());
            TabulatedFunction second = TabulatedFunctionMapper.fromPoints(secondPoints, FactoryHolder.getInstance().getFactory());

            TabulatedFunction result;
            switch (operation) {
                case "add":
                    result = ops.sum(first, second);
                    break;
                case "subtract":
                    result = ops.subtract(first, second);
                    break;
                case "multiply":
                    result = ops.multiply(first, second);
                    break;
                case "divide":
                    result = ops.divide(first, second);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестная операция: " + operation);
            }

            writeFunction(resp, result);
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private void writeFunction(HttpServletResponse resp, TabulatedFunction function) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), function.getPoints());
    }
}
