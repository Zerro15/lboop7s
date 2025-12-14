package com.example.lab5.manual.ui;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.functions.tabulated.TabulatedPoint;
import com.example.lab5.manual.service.FunctionService;
import com.example.lab5.manual.service.JwtService;
import com.example.lab5.manual.service.PointService;
import com.example.lab5.manual.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Работа с пользовательскими сохраненными функциями (для построения графиков).
 */
@WebServlet("/ui/api/user-functions/*")
public class UserFunctionServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();
    private final UserService userService = new UserService();
    private final FunctionService functionService = new FunctionService();
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO user = resolveUser(req);
            List<FunctionDTO> functions = functionService.getFunctionsByUserId(user.getId());
            ArrayNode arrayNode = objectMapper.createArrayNode();

            for (FunctionDTO fn : functions) {
                List<PointDTO> points = pointService.getPointsByFunctionId(fn.getId());
                ObjectNode node = objectMapper.createObjectNode();
                node.put("id", fn.getId());
                node.put("name", fn.getName());
                ArrayNode pointsNode = objectMapper.createArrayNode();
                points.forEach(pt -> {
                    ObjectNode p = objectMapper.createObjectNode();
                    p.put("x", pt.getXValue());
                    p.put("y", pt.getYValue());
                    pointsNode.add(p);
                });
                node.set("points", pointsNode);
                arrayNode.add(node);
            }

            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), arrayNode);
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO user = resolveUser(req);
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            String name = payload.hasNonNull("name") ? payload.get("name").asText().trim() : "";
            List<TabulatedPoint> points = payload.has("points")
                    ? objectMapper.convertValue(payload.get("points"), new TypeReference<List<TabulatedPoint>>() {})
                    : new ArrayList<>();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Введите название функции");
            }
            if (points.isEmpty()) {
                throw new IllegalArgumentException("Добавьте точки для сохранения графика");
            }
            if (!functionService.validateFunctionName(user.getId(), name)) {
                throw new IllegalArgumentException("Функция с таким именем уже сохранена");
            }

            Long functionId = functionService.createFunction(user.getId(), name, "user-defined");
            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            for (TabulatedPoint pt : points) {
                xs.add(pt.getX());
                ys.add(pt.getY());
            }
            pointService.createPointsBatch(functionId, xs, ys);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("id", functionId);
            response.put("name", name);
            response.set("points", objectMapper.valueToTree(points));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private UserDTO resolveUser(HttpServletRequest req) {
        Object principalObj = req.getAttribute("principal");
        if (!(principalObj instanceof JwtService.UserPrincipal)) {
            throw new IllegalArgumentException("Требуется авторизация");
        }
        JwtService.UserPrincipal principal = (JwtService.UserPrincipal) principalObj;
        Optional<UserDTO> user = userService.getUserByLogin(principal.getLogin());
        return user.orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
