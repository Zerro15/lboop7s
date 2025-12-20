package com.example.lab5.manual.ui;

import com.example.lab5.manual.functions.MathFunctionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет отдаёт список доступных функций с локализованными названиями.
 */
@WebServlet("/ui/api/functions")
public class MathFunctionServlet extends HttpServlet {
    private final MathFunctionRegistry registry = MathFunctionRegistry.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), registry.listLocalizedNames());
    }
}
