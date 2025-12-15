package com.example.lab5.functions;

public class TabulatedFunction implements Insertable, Removable {
    private double[] xValues;
    private double[] yValues;

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

    @Override
    public void insert(double x, double y) {
        int position = findInsertPosition(x);
        if (position < xValues.length && xValues[position] == x) {
            throw new IllegalArgumentException("Значение X уже существует и должно быть уникальным");
        }

        double[] newX = new double[xValues.length + 1];
        double[] newY = new double[yValues.length + 1];

        System.arraycopy(xValues, 0, newX, 0, position);
        System.arraycopy(yValues, 0, newY, 0, position);

        newX[position] = x;
        newY[position] = y;

        System.arraycopy(xValues, position, newX, position + 1, xValues.length - position);
        System.arraycopy(yValues, position, newY, position + 1, yValues.length - position);

        validateOrder(newX);

        this.xValues = newX;
        this.yValues = newY;
    }

    @Override
    public void remove(int index) {
        checkIndex(index);
        if (xValues.length <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: должно остаться минимум две");
        }

        double[] newX = new double[xValues.length - 1];
        double[] newY = new double[yValues.length - 1];

        System.arraycopy(xValues, 0, newX, 0, index);
        System.arraycopy(yValues, 0, newY, 0, index);
        System.arraycopy(xValues, index + 1, newX, index, xValues.length - index - 1);
        System.arraycopy(yValues, index + 1, newY, index, yValues.length - index - 1);

        this.xValues = newX;
        this.yValues = newY;
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

    private int findInsertPosition(double x) {
        int low = 0;
        int high = xValues.length;
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (xValues[mid] < x) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    private void validateOrder(double[] values) {
        for (int i = 1; i < values.length; i++) {
            if (values[i] <= values[i - 1]) {
                throw new IllegalArgumentException("После вставки нарушен строгий порядок X");
            }
        }
    }
}
