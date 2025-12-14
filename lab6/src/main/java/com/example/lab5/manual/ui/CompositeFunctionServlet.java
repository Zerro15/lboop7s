package com.example.lab5.manual.ui;

import com.example.lab5.manual.functions.MathFunction;
import com.example.lab5.manual.functions.MathFunctionRegistry;
import com.example.lab5.manual.functions.impl.CompositeFunction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Регистрация составных функций, доступных для табуляции.
 */
@WebServlet("/ui/api/functions/composite")
public class CompositeFunctionServlet extends HttpServlet {
    private final MathFunctionRegistry registry = MathFunctionRegistry.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String name = payload.get("name").asText();
            int priority = payload.get("priority").asInt();
            List<String> chain = objectMapper.convertValue(payload.get("components"), new TypeReference<List<String>>() {});

            Map<String, MathFunction> available = registry.getFunctions();
            List<MathFunction> functions = new ArrayList<>();
            for (String key : chain) {
                MathFunction fn = available.get(key);
                if (fn == null) {
                    throw new IllegalArgumentException("Функция " + key + " недоступна для композиции");
                }
                functions.add(fn);
            }
            CompositeFunction composite = new CompositeFunction(functions);
            registry.registerCustom(name, composite, priority);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), registry.listLocalizedNames());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }
}
