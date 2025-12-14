package com.example.lab5.ui.factory;

import com.example.lab5.ui.dto.PointDto;
import com.example.lab5.ui.math.MathFunction;
import com.example.lab5.ui.model.TabulatedFunction;

import java.util.List;

public interface TabulatedFunctionFactory {
    TabulatedFunction createFromArrays(List<PointDto> points);

    TabulatedFunction createFromMathFunction(MathFunction mathFunction, double leftBound, double rightBound, int pointsCount);
}
