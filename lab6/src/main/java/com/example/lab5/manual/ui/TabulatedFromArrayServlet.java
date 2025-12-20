package com.example.lab5.manual.ui;

import com.example.lab5.manual.config.FactoryHolder;
import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет для создания табулированной функции по массивам X и Y.
 */
@WebServlet("/ui/api/tabulated/array")
public class TabulatedFromArrayServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            ArrayNode xValuesNode = (ArrayNode) payload.get("xValues");
            ArrayNode yValuesNode = (ArrayNode) payload.get("yValues");

            if (xValuesNode == null || yValuesNode == null) {
                throw new ValidationException("Требуются массивы xValues и yValues");
            }

            double[] xValues = toArray(xValuesNode);
            double[] yValues = toArray(yValuesNode);
            TabulatedFunctionFactory factory = FactoryHolder.getInstance().getFactory();
            TabulatedFunction function = factory.create(xValues, yValues);

            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), function.getPoints());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private double[] toArray(ArrayNode node) {
        double[] result = new double[node.size()];
        for (int i = 0; i < node.size(); i++) {
            result[i] = node.get(i).asDouble();
        }
        return result;
    }
}
