package com.example.lab5.manual.ui;

import com.example.lab5.manual.functions.exception.TableSizeLimitExceededException;
import com.example.lab5.manual.functions.exception.UnknownFunctionException;
import com.example.lab5.manual.functions.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Единая точка формирования ответов об ошибках.
 */
public class ExceptionResponder {
    private static final Logger logger = Logger.getLogger(ExceptionResponder.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handle(HttpServletResponse response, Exception exception) throws IOException {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "Неизвестная ошибка";

        if (exception instanceof NumberFormatException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = "Ожидается числовой ввод";
        } else if (exception instanceof TableSizeLimitExceededException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = exception.getMessage();
        } else if (exception instanceof UnknownFunctionException) {
            status = HttpServletResponse.SC_NOT_FOUND;
            message = exception.getMessage();
        } else if (exception instanceof ValidationException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = exception.getMessage();
        }

        logger.log(Level.WARNING, "UI error", exception);
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }
}
