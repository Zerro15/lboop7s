package com.example.lab5.functions;

public class TabulatedFunction {
    private final double[] xValues;
    private final double[] yValues;

    public TabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null) {
            throw new IllegalArgumentException("Массивы X и Y должны быть заданы");
        }
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Количество X и Y должно совпадать");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Необходимо минимум две точки");
        }

        this.xValues = xValues.clone();
        this.yValues = yValues.clone();

        for (int i = 1; i < this.xValues.length; i++) {
            if (this.xValues[i] <= this.xValues[i - 1]) {
                throw new IllegalArgumentException("Массив X должен быть строго возрастающим");
            }
        }
    }

    public int size() {
        return xValues.length;
    }

    public double getX(int index) {
        checkIndex(index);
        return xValues[index];
    }

    public double getY(int index) {
        checkIndex(index);
        return yValues[index];
    }

    public void setY(int index, double value) {
        checkIndex(index);
        yValues[index] = value;
    }

    public double[] getXValues() {
        return xValues.clone();
    }

    public double[] getYValues() {
        return yValues.clone();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= xValues.length) {
            throw new IndexOutOfBoundsException("Некорректный индекс точки: " + index);
        }
    }
}
