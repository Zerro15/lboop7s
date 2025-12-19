package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    @Autowired
    private InMemoryFunctionStore functionStore;

    public Point createPoint(Long functionId, Double xValue, Double yValue) {
        logger.info("Создание точки: function={}, x={}, y={}", functionId, xValue, yValue);

        Optional<Function> function = functionStore.findFunction(functionId);
        if (!function.isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            throw new IllegalArgumentException("Function with ID " + functionId + " does not exist");
        }

        Point point = new Point(xValue, yValue, function.get());
        Point savedPoint = functionStore.savePoint(functionId, point);
        logger.debug("Создана точка с ID: {}", savedPoint.getId());
        return savedPoint;
    }

    public int generateFunctionPoints(Long functionId, String functionType, double start, double end, double step) {
        logger.info("Генерация точек для функции {}: type={}, range=[{}, {}], step={}",
                (Object) functionId, (Object) functionType, (Object) start, (Object) end, (Object) step);

        Optional<Function> function = functionStore.findFunction(functionId);
        if (!function.isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            return 0;
        }

        if (step <= 0) {
            throw new IllegalArgumentException("Шаг генерации точек должен быть положительным");
        }
        if (end < start) {
            throw new IllegalArgumentException("Правая граница диапазона должна быть больше или равна левой");
        }

        List<Point> points = new ArrayList<>();
        int pointCount = 0;

        for (Double x = (Double) start; x <= end; x += step) {
            Double y = (Double) calculateFunction(functionType, x);
            points.add(new Point(x, y, function.get()));
            pointCount++;
        }

        if (!points.isEmpty()) {
            functionStore.savePoints(functionId, points);
            logger.info("Сгенерировано {} точек для функции {}", Optional.of(pointCount), functionId);
        }

        return pointCount;
    }

    public Point updatePoint(Long id, Long functionId, Double xValue, Double yValue) {
        logger.info("Обновление точки с ID: {}", id);

        Optional<Point> existingPoint = functionStore.findPointById(id);
        if (existingPoint.isPresent()) {
            Optional<Function> function = functionStore.findFunction(functionId);
            if (!function.isPresent()) {
                logger.error("Функция с ID {} не существует", functionId);
                return null;
            }

            Point point = existingPoint.get();
            point.setFunction(function.get());
            point.setXValue(xValue);
            point.setYValue(yValue);

            functionStore.savePoint(functionId, point);
            logger.info("Точка с ID {} успешно обновлена", id);
            return point;
        }

        logger.warn("Точка с ID {} не найдена для обновления", id);
        return null;
    }

    public boolean deletePoint(Long id) {
        logger.info("Удаление точки с ID: {}", id);

        boolean removed = functionStore.deletePoint(id);
        if (removed) {
            logger.info("Точка с ID {} удалена", id);
            return true;
        }

        logger.warn("Точка с ID {} не найдена для удаления", id);
        return false;
    }

    private double calculateFunction(String functionType, double x) {
        return switch (functionType.toLowerCase()) {
            case "linear" -> x;
            case "quadratic" -> x * x;
            case "cubic" -> x * x * x;
            case "sin" -> Math.sin(x);
            case "cos" -> Math.cos(x);
            case "exp" -> Math.exp(x);
            case "log" -> Math.log(x);
            default -> x;
        };
    }

    public Optional<Point> getPointById(Long id) {
        logger.debug("Поиск точки по ID: {}", id);
        return functionStore.findPointById(id);
    }

    public List<Point> getPointsByFunctionId(Long functionId) {
        logger.debug("Поиск точек функции с ID: {}", functionId);
        return functionStore.findPointsByFunctionId(functionId);
    }

    public List<Point> getAllPoints() {
        logger.debug("Получение всех точек");
        return functionStore.findAllFunctions().stream()
                .flatMap(f -> functionStore.findPointsByFunctionId(f.getId()).stream())
                .toList();
    }
}
