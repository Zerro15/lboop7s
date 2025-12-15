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

    // ДОБАВЛЕННЫЙ МЕТОД apply()
    /**
     * Вычисляет значение функции в точке x.
     * Если x находится между двумя точками, используется линейная интерполяция.
     * Если x за пределами массива, возвращается значение на ближайшем конце.
     *
     * @param x точка для вычисления
     * @return значение функции в точке x
     */
    public double apply(double x) {
        // Проверка на пустой массив
        if (xValues == null || xValues.length == 0) {
            throw new IllegalStateException("Функция не инициализирована");
        }

        // Если x меньше или равен первому значению
        if (x <= xValues[0]) {
            return yValues[0];
        }

        // Если x больше или равен последнему значению
        if (x >= xValues[xValues.length - 1]) {
            return yValues[yValues.length - 1];
        }

        // Поиск интервала, в котором находится x
        for (int i = 0; i < xValues.length - 1; i++) {
            double x1 = xValues[i];
            double x2 = xValues[i + 1];

            // Если x точно совпадает с одной из точек
            if (Math.abs(x - x1) < 1e-10) {
                return yValues[i];
            }
            if (Math.abs(x - x2) < 1e-10) {
                return yValues[i + 1];
            }

            // Если x находится между x1 и x2
            if (x > x1 && x < x2) {
                // Линейная интерполяция
                double y1 = yValues[i];
                double y2 = yValues[i + 1];
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        // Не должно сюда дойти, но на всякий случай
        throw new IllegalStateException("Не удалось вычислить значение для x = " + x);
    }

    /**
     * Альтернативная версия apply с бинарным поиском (более эффективная для больших массивов)
     */
    public double applyBinary(double x) {
        if (xValues == null || xValues.length == 0) {
            throw new IllegalStateException("Функция не инициализирована");
        }

        if (x <= xValues[0]) {
            return yValues[0];
        }
        if (x >= xValues[xValues.length - 1]) {
            return yValues[yValues.length - 1];
        }

        // Бинарный поиск для нахождения интервала
        int left = 0;
        int right = xValues.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Точное совпадение
            if (Math.abs(x - xValues[mid]) < 1e-10) {
                return yValues[mid];
            }

            // Проверяем интервал
            if (mid < xValues.length - 1 && x > xValues[mid] && x < xValues[mid + 1]) {
                // Линейная интерполяция
                double x1 = xValues[mid];
                double x2 = xValues[mid + 1];
                double y1 = yValues[mid];
                double y2 = yValues[mid + 1];
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }

            if (x < xValues[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        // Если не нашли точного интервала (маловероятно)
        return linearInterpolation(x);
    }

    // Вспомогательный метод для линейной интерполяции
    private double linearInterpolation(double x) {
        for (int i = 0; i < xValues.length - 1; i++) {
            if (x > xValues[i] && x < xValues[i + 1]) {
                double x1 = xValues[i];
                double x2 = xValues[i + 1];
                double y1 = yValues[i];
                double y2 = yValues[i + 1];
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        throw new IllegalStateException("Не удалось интерполировать значение для x = " + x);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TabulatedFunction[");
        for (int i = 0; i < Math.min(5, xValues.length); i++) {
            sb.append("(").append(xValues[i]).append(", ").append(yValues[i]).append(")");
            if (i < Math.min(5, xValues.length) - 1) {
                sb.append(", ");
            }
        }
        if (xValues.length > 5) {
            sb.append(", ... (").append(xValues.length - 5).append(" more)");
        }
        sb.append("]");
        return sb.toString();
    }
}