package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.DifferentiationRequest;
import com.example.lab5.framework.dto.DifferentiationResponse;
import com.example.lab5.framework.dto.OperationRequest;
import com.example.lab5.framework.dto.OperationResponse;
import com.example.lab5.framework.dto.PointDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class OperationService {

    public OperationResponse apply(OperationRequest request) {
        validatePayload(request);

        List<PointDTO> left = normalize(request.getLeftPoints());
        List<PointDTO> right = normalize(request.getRightPoints());

        List<PointDTO> result = new ArrayList<>();
        for (int i = 0; i < left.size(); i++) {
            PointDTO a = left.get(i);
            PointDTO b = right.get(i);
            if (!Objects.equals(a.getX(), b.getX())) {
                throw new IllegalArgumentException("Значения X должны совпадать у обеих функций");
            }
            double y;
            switch (request.getOperation().toLowerCase(Locale.ROOT)) {
                case "add":
                    y = a.getY() + b.getY();
                    break;
                case "sub":
                    y = a.getY() - b.getY();
                    break;
                case "mul":
                    y = a.getY() * b.getY();
                    break;
                case "div":
                    if (b.getY() == 0.0) {
                        throw new IllegalArgumentException("Деление на ноль недопустимо");
                    }
                    y = a.getY() / b.getY();
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестная операция: " + request.getOperation());
            }
            if (!Double.isFinite(y)) {
                throw new IllegalArgumentException("Некорректный результат вычисления");
            }
            PointDTO point = new PointDTO();
            point.setX(a.getX());
            point.setY(y);
            result.add(point);
        }

        OperationResponse response = new OperationResponse();
        response.setPoints(result);
        return response;
    }

    public DifferentiationResponse differentiate(DifferentiationRequest request) {
        List<PointDTO> source = normalize(request.getPoints());
        if (source.size() < 2) {
            throw new IllegalArgumentException("Минимум две точки для дифференцирования");
        }

        List<PointDTO> derivative = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            PointDTO current = source.get(i);
            double slope;
            if (i == 0) {
                double dx = source.get(i + 1).getX() - current.getX();
                slope = (source.get(i + 1).getY() - current.getY()) / dx;
            } else if (i == source.size() - 1) {
                double dx = current.getX() - source.get(i - 1).getX();
                slope = (current.getY() - source.get(i - 1).getY()) / dx;
            } else {
                double dx1 = current.getX() - source.get(i - 1).getX();
                double dx2 = source.get(i + 1).getX() - current.getX();
                double dy1 = current.getY() - source.get(i - 1).getY();
                double dy2 = source.get(i + 1).getY() - current.getY();
                slope = ((dy1 / dx1) + (dy2 / dx2)) / 2.0;
            }
            if (!Double.isFinite(slope)) {
                throw new IllegalArgumentException("Шаг между точками недопустим для производной");
            }
            PointDTO p = new PointDTO();
            p.setX(current.getX());
            p.setY(slope);
            derivative.add(p);
        }

        DifferentiationResponse response = new DifferentiationResponse();
        response.setPoints(derivative);
        return response;
    }

    private void validatePayload(OperationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос пустой");
        }
        if (request.getLeftPoints() == null || request.getRightPoints() == null) {
            throw new IllegalArgumentException("Не заданы обе функции для операции");
        }
        if (request.getLeftPoints().size() != request.getRightPoints().size()) {
            throw new IllegalArgumentException("Функции должны иметь одинаковое количество точек");
        }
        if (request.getLeftPoints().size() < 2) {
            throw new IllegalArgumentException("Минимум две точки для операции");
        }
        if (request.getOperation() == null || request.getOperation().isBlank()) {
            throw new IllegalArgumentException("Не указана операция");
        }
    }

    private List<PointDTO> normalize(List<PointDTO> points) {
        List<PointDTO> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparing(PointDTO::getX));
        for (PointDTO p : sorted) {
            if (p.getX() == null || p.getY() == null) {
                throw new IllegalArgumentException("Точки должны содержать числовые значения x и y");
            }
            if (!Double.isFinite(p.getX()) || !Double.isFinite(p.getY())) {
                throw new IllegalArgumentException("Некорректные значения точек");
            }
        }
        return sorted;
    }
}
