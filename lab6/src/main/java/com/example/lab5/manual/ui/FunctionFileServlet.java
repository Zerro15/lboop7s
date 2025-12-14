package com.example.lab5.manual.ui;

import com.example.lab5.manual.config.FactoryHolder;
import com.example.lab5.manual.functions.io.FunctionsIO;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;
import com.example.lab5.manual.functions.tabulated.TabulatedFunctionMapper;
import com.example.lab5.manual.functions.tabulated.TabulatedPoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

/**
 * Сохранение и загрузка табулированных функций.
 */
@WebServlet("/ui/api/function-files/*")
@MultipartConfig
public class FunctionFileServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExceptionResponder exceptionResponder = new ExceptionResponder();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && path.contains("upload")) {
            handleUpload(req, resp);
        } else if (path != null && path.contains("download")) {
            handleDownload(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            Part file = req.getPart("file");
            if (file == null || file.getSize() == 0) {
                throw new IllegalArgumentException("Файл не выбран");
            }
            TabulatedFunction function = FunctionsIO.deserialize(file.getInputStream());
            resp.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(resp.getWriter(), function.getPoints());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }

    private void handleDownload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectNode payload = objectMapper.readValue(req.getInputStream(), ObjectNode.class);
            List<TabulatedPoint> points = objectMapper.convertValue(payload.get("function"), new TypeReference<List<TabulatedPoint>>() {});
            TabulatedFunction function = TabulatedFunctionMapper.fromPoints(points, FactoryHolder.getInstance().getFactory());
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=tabulated-function.bin");
            FunctionsIO.serialize(function, resp.getOutputStream());
        } catch (Exception ex) {
            exceptionResponder.handle(resp, ex);
        }
    }
}
