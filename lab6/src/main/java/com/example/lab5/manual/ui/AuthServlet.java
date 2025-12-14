package com.example.lab5.manual.ui;

import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.service.JwtService;
import com.example.lab5.manual.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Регистрация и авторизация с выдачей JWT.
 */
@WebServlet("/ui/api/auth/*")
public class AuthServlet extends HttpServlet {
    private static final JwtService JWT = new JwtService("manual-secret-key");
    private final UserService userService = new UserService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path != null && path.contains("register")) {
            handleRegister(req, resp);
        } else if (path != null && path.contains("login")) {
            handleLogin(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String login = payload.get("login").asText();
            String password = payload.get("password").asText();
            if (login.isBlank() || password.isBlank()) {
                throw new IllegalArgumentException("Логин и пароль обязательны");
            }
            UserDTO user = userService.register(login, password);
            issueToken(resp, user.getLogin(), user.getRole());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String login = payload.get("login").asText();
            String password = payload.get("password").asText();
            Optional<UserDTO> userOpt = userService.getUserByLogin(login);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("Пользователь не найден. Зарегистрируйтесь и выполните вход.");
            }
            UserDTO user = userOpt.get();
            if (!user.getPassword().equals(password)) {
                throw new IllegalArgumentException("Неверный пароль");
            }
            issueToken(resp, user.getLogin(), user.getRole());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private void issueToken(HttpServletResponse resp, String login, String role) throws IOException {
        String token = JWT.issueToken(login, role);
        ObjectNode response = objectMapper.createObjectNode();
        response.put("token", token);
        response.put("login", login);
        response.put("role", role);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(response.toString());
    }
}
