package com.example.lab5.functions;

/**
 * Интерфейс для функций, поддерживающих вставку новой точки.
 */
public interface Insertable {

    /**
     * Вставляет новую точку в табулированную функцию.
     *
     * @param x значение X новой точки
     * @param y значение Y новой точки
     */
    void insert(double x, double y);
}
