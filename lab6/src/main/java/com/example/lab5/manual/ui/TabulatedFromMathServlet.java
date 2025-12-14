package com.example.lab5.manual.ui;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.MathFunctionRegistry;
import com.example.lab5.manual.functions.exception.UnknownFunctionException;
import com.example.lab5.manual.functions.factory.ArrayTabulatedFunctionFactory;
import com.example.lab5.manual.functions.factory.TabulatedFunctionFactory;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Сервлет для создания табулированной функции из произвольной MathFunction.
 */
@WebServlet("/ui/api/tabulated/math")
public class TabulatedFromMathServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
    private final MathFunctionRegistry registry = new MathFunctionRegistry();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String functionName = payload.get("functionName").asText();
            double from = Double.parseDouble(payload.get("from").asText());
            double to = Double.parseDouble(payload.get("to").asText());
            int count = payload.get("count").asInt();

            MathFunction function = resolveFunction(functionName);
            TabulatedFunction tabulated = factory.create(function, from, to, count);

            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), tabulated.getPoints());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private MathFunction resolveFunction(String name) {
        for (Map.Entry<String, MathFunction> entry : registry.getFunctions().entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        throw new UnknownFunctionException(name);
    }
}
