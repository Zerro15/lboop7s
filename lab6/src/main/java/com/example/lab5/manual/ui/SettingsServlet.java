package com.example.lab5.manual.ui;

import com.example.lab5.manual.config.FactoryHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Управление выбранной пользователем фабрикой.
 */
@WebServlet("/ui/api/settings/factory")
public class SettingsServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FactoryHolder.FactoryType type = FactoryHolder.getInstance().getType();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", type.name());
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), node);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String type = payload.get("type").asText();
            FactoryHolder.FactoryType factoryType = FactoryHolder.FactoryType.valueOf(type);
            FactoryHolder.getInstance().switchFactory(factoryType);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }
}
