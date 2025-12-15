package com.example.lab5.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TabulatedIntegralCalculator {
    private static final int MAX_THREADS = 16;

    public double integrate(TabulatedFunction function, Integer threads) {
        if (function == null) {
            throw new IllegalArgumentException("Функция для интегрирования не задана");
        }
        int segments = function.size() - 1;
        if (segments < 1) {
            throw new IllegalArgumentException("Недостаточно точек для интегрирования");
        }

        int requestedThreads = threads == null ? 1 : threads;
        if (requestedThreads < 1 || requestedThreads > MAX_THREADS) {
            throw new IllegalArgumentException("Количество потоков должно быть от 1 до " + MAX_THREADS);
        }

        int actualThreads = Math.min(requestedThreads, segments);
        if (actualThreads == 1) {
            return integrateRange(function, 0, segments);
        }

        ExecutorService executor = Executors.newFixedThreadPool(actualThreads);
        try {
            List<Callable<Double>> tasks = new ArrayList<>();
            int chunk = (segments + actualThreads - 1) / actualThreads;
            for (int start = 0; start < segments; start += chunk) {
                int end = Math.min(start + chunk, segments);
                tasks.add(() -> integrateRange(function, start, end));
            }

            double total = 0.0;
            List<Future<Double>> results = executor.invokeAll(tasks);
            for (Future<Double> future : results) {
                try {
                    total += future.get();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new IllegalStateException("Ошибка при вычислении интеграла", cause);
                }
            }
            return total;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Вычисление интеграла было прервано", ie);
        } finally {
            executor.shutdown();
        }
    }

    private double integrateRange(TabulatedFunction function, int startIndex, int endIndexExclusive) {
        double sum = 0.0;
        for (int i = startIndex; i < endIndexExclusive; i++) {
            double h = function.getX(i + 1) - function.getX(i);
            double area = h * (function.getY(i) + function.getY(i + 1)) / 2.0;
            sum += area;
        }
        return sum;
    }
}
