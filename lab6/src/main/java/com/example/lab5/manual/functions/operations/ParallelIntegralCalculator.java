package com.example.lab5.manual.functions.operations;

import com.example.lab5.manual.functions.exception.ValidationException;
import com.example.lab5.manual.functions.tabulated.TabulatedFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Параллельное вычисление определённого интеграла методом трапеций.
 */
public class ParallelIntegralCalculator {
    public double integrate(TabulatedFunction function, int threads) {
        if (threads < 1) {
            throw new ValidationException("Количество потоков должно быть положительным");
        }
        int maxThreads = Math.max(1, Math.min(function.getCount() - 1, Runtime.getRuntime().availableProcessors()));
        int threadCount = Math.min(threads, maxThreads);
        int segments = function.getCount() - 1;
        int chunk = (int) Math.ceil((double) segments / threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Callable<Double>> tasks = new ArrayList<>();
            for (int start = 0; start < segments; start += chunk) {
                int from = start;
                int to = Math.min(start + chunk, segments);
                tasks.add(() -> integrateRange(function, from, to));
            }
            double sum = 0.0;
            List<Future<Double>> results = executor.invokeAll(tasks);
            for (Future<Double> f : results) {
                sum += f.get();
            }
            return sum;
        } catch (Exception e) {
            throw new ValidationException("Не удалось вычислить интеграл: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private double integrateRange(TabulatedFunction fn, int from, int to) {
        double area = 0.0;
        for (int i = from; i < to; i++) {
            double x1 = fn.getX(i);
            double x2 = fn.getX(i + 1);
            double y1 = fn.getY(i);
            double y2 = fn.getY(i + 1);
            area += 0.5 * (y1 + y2) * (x2 - x1);
        }
        return area;
    }
}
