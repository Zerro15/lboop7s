package com.example.lab5.ui.factory;

import com.example.lab5.ui.dto.PointDto;
import com.example.lab5.ui.math.MathFunction;
import com.example.lab5.ui.model.TabulatedFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DefaultTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction createFromArrays(List<PointDto> points) {
        validatePoints(points);
        List<PointDto> sorted = new ArrayList<>(points);
        sorted.sort(Comparator.comparing(PointDto::getX));
        return new TabulatedFunction(sorted, "manual");
    }

    @Override
    public TabulatedFunction createFromMathFunction(MathFunction mathFunction, double leftBound, double rightBound, int pointsCount) {
        if (mathFunction == null) {
            throw new IllegalArgumentException("Не выбрана математическая функция");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (Double.isNaN(leftBound) || Double.isNaN(rightBound)) {
            throw new IllegalArgumentException("Границы интервала заданы некорректно");
        }
        if (leftBound >= rightBound) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        double step = (rightBound - leftBound) / (pointsCount - 1);
        List<PointDto> generated = new ArrayList<>();

        for (int i = 0; i < pointsCount; i++) {
            double x = leftBound + i * step;
            double y = mathFunction.apply(x);

            if (Double.isNaN(y) || Double.isInfinite(y)) {
                throw new IllegalArgumentException("Функция не определена для значения x=" + x);
            }

            generated.add(new PointDto(x, y));
        }

        return new TabulatedFunction(generated, mathFunction.getLocalizedName());
    }

    private void validatePoints(List<PointDto> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Необходимо задать хотя бы две точки");
        }
        if (points.size() < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        Set<Double> usedX = new HashSet<>();
        for (PointDto point : points) {
            if (point == null || point.getX() == null || point.getY() == null) {
                throw new IllegalArgumentException("Каждая точка должна содержать числовые значения X и Y");
            }
            if (Double.isNaN(point.getX()) || Double.isNaN(point.getY()) ||
                    Double.isInfinite(point.getX()) || Double.isInfinite(point.getY())) {
                throw new IllegalArgumentException("Значения X и Y должны быть конечными числами");
            }
            if (!usedX.add(point.getX())) {
                throw new IllegalArgumentException("Значения X должны быть уникальными");
            }
        }
    }
}
