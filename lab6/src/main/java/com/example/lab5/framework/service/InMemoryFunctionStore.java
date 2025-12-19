package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Простое хранилище табулированных функций в памяти.
 * В базе данных сохраняются только учетные данные пользователя,
 * поэтому функции и точки управляются здесь без участия репозиториев.
 */
@Component
public class InMemoryFunctionStore {

    private final AtomicLong functionIdSeq = new AtomicLong(1);
    private final AtomicLong pointIdSeq = new AtomicLong(1);
    private final Map<Long, Function> functions = new ConcurrentHashMap<>();
    private final Map<Long, List<Point>> functionPoints = new ConcurrentHashMap<>();

    public Function saveFunction(Function function) {
        if (function.getId() == null) {
            function.setId(functionIdSeq.getAndIncrement());
        }
        functions.put(function.getId(), function);
        functionPoints.putIfAbsent(function.getId(), new ArrayList<>());
        return function;
    }

    public List<Point> savePoints(Long functionId, List<Point> points) {
        List<Point> stored = functionPoints.computeIfAbsent(functionId, k -> new ArrayList<>());
        for (Point point : points) {
            if (point.getId() == null) {
                point.setId(pointIdSeq.getAndIncrement());
            }
            stored.add(point);
        }
        return stored;
    }

    public Point savePoint(Long functionId, Point point) {
        if (point.getId() == null) {
            point.setId(pointIdSeq.getAndIncrement());
        }
        functionPoints.computeIfAbsent(functionId, k -> new ArrayList<>()).add(point);
        return point;
    }

    public Optional<Function> findFunction(Long id) {
        return Optional.ofNullable(functions.get(id));
    }

    public List<Function> findAllFunctions() {
        return new ArrayList<>(functions.values());
    }

    public List<Function> findFunctionsByUserId(Long userId) {
        return functions.values().stream()
                .filter(f -> f.getUser() != null && f.getUser().getId().equals(userId))
                .toList();
    }

    public List<Point> findPointsByFunctionId(Long functionId) {
        return new ArrayList<>(functionPoints.getOrDefault(functionId, Collections.emptyList()));
    }

    public Optional<Point> findPointById(Long pointId) {
        return functionPoints.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getId().equals(pointId))
                .findFirst();
    }

    public boolean deleteFunction(Long id) {
        Function removed = functions.remove(id);
        functionPoints.remove(id);
        return removed != null;
    }

    public boolean deletePoint(Long pointId) {
        for (List<Point> points : functionPoints.values()) {
            Optional<Point> match = points.stream()
                    .filter(p -> p.getId().equals(pointId))
                    .findFirst();
            if (match.isPresent()) {
                points.remove(match.get());
                return true;
            }
        }
        return false;
    }
}
