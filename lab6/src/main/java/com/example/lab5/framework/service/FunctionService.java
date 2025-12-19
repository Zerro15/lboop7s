package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.CreateFromArraysRequest;
import com.example.lab5.framework.dto.CreateFromMathRequest;
import com.example.lab5.framework.dto.EvaluateResponse;
import com.example.lab5.framework.dto.EvaluateTabulatedRequest;
import com.example.lab5.framework.dto.EvaluateTabulatedResponse;
import com.example.lab5.framework.dto.FunctionCreationResult;
import com.example.lab5.framework.dto.TabulatedFunctionPayload;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.UserRepository;
import com.example.lab5.functions.MathFunction;
import com.example.lab5.functions.TabulatedFunction;
import com.example.lab5.functions.TabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFunctionService mathFunctionService;

    @Autowired
    private TabulatedFunctionFactoryHolder factoryHolder;

    @Autowired
    private InMemoryFunctionStore functionStore;

    // Функции хранятся только в памяти. Проверяем пользователя по БД, но сами функции не пишем в базу.
    public Function createFunction(Long userId, String name, String signature) {
        logger.info("Создание функции: user={}, name={}, signature={}", userId, name, signature);

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            logger.error("Пользователь с ID {} не существует", userId);
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        Function function = new Function(name, signature, user.get());
        functionStore.saveFunction(function);
        logger.info("Создана функция с ID: {}", function.getId());
        return function;
    }

    // НОВЫЕ МЕТОДЫ ДЛЯ LAB 7
    @Transactional
    public FunctionCreationResult createFromArrays(Long userId, String name,
                                                   List<CreateFromArraysRequest.PointData> pointsData,
                                                   String factoryType) {
        logger.info("Создание функции из массивов: user={}, name={}, points={}, factory={}",
                (Object) userId, (Object) name, (Object) pointsData.size(), factoryType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        // Создаем функцию
        String effectiveFactoryKey = factoryHolder.resolveKey(factoryType);
        TabulatedFunctionFactory chosenFactory = factoryHolder.resolveFactory(factoryType);

        double[] xValues = pointsData.stream().mapToDouble(CreateFromArraysRequest.PointData::getX).toArray();
        double[] yValues = pointsData.stream().mapToDouble(CreateFromArraysRequest.PointData::getY).toArray();

        TabulatedFunction tabulatedFunction = chosenFactory.create(xValues, yValues);

        double[] resolvedX = tabulatedFunction.getXValues();
        double[] resolvedY = tabulatedFunction.getYValues();
        Function function = new Function();
        function.setName(name);
        function.setUser(user);
        function.setSignature("tabulated");
        function.setFactoryType(effectiveFactoryKey);
        function.setCreationMethod("from_arrays");
        function.setPointsCount(resolvedX.length);
        function.setLeftBound(Double.valueOf(resolvedX[0]));
        function.setRightBound(Double.valueOf(resolvedX[resolvedX.length - 1]));

        functionStore.saveFunction(function);
        logger.info("Функция создана с ID: {}", function.getId());
        for (int i = 0; i < resolvedX.length; i++) {
            Point point = new Point();
            point.setXValue(Double.valueOf(resolvedX[i]));
            point.setYValue(Double.valueOf(resolvedY[i]));
            point.setFunction(function);
            functionStore.savePoint(function.getId(), point);
        }

        logger.info("Создано {} точек для функции {}", Optional.of(resolvedX.length), function.getId());
        return new FunctionCreationResult(function, resolvedX, resolvedY);
    }

    @Transactional
    public FunctionCreationResult createFromMathFunction(Long userId, String name, String mathFunctionKey,
                                                         Integer pointsCount, Double leftBound, Double rightBound,
                                                         String factoryType) {
        logger.info("Создание функции из MathFunction: user={}, name={}, functionKey={}, points={}, bounds=[{}, {}], factory={}",
                userId, name, mathFunctionKey, pointsCount, leftBound, rightBound, factoryType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        // Проверяем корректность параметров
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftBound >= rightBound) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Создаем функцию
        String effectiveFactoryKey = factoryHolder.resolveKey(factoryType);
        TabulatedFunctionFactory chosenFactory = factoryHolder.resolveFactory(factoryType);

        Function function = new Function();
        function.setName(name);
        function.setUser(user);
        function.setSignature("math_function_tabulated");
        function.setMathFunctionKey(mathFunctionKey);
        function.setFactoryType(effectiveFactoryKey);
        function.setCreationMethod("from_math_function");
        function.setPointsCount(pointsCount);
        function.setLeftBound(leftBound);
        function.setRightBound(rightBound);

        functionStore.saveFunction(function);
        logger.info("Функция создана с ID: {}", function.getId());

        MathFunction mathFunction = mathFunctionService.getFunctionByKey(mathFunctionKey);
        if (mathFunction == null) {
            throw new IllegalArgumentException("Неизвестная математическая функция: " + mathFunctionKey);
        }

        TabulatedFunction tabulated = chosenFactory.create(mathFunction, leftBound, rightBound, pointsCount);

        double[] xValues = tabulated.getXValues();
        double[] yValues = tabulated.getYValues();

        for (int i = 0; i < xValues.length; i++) {
            Point point = new Point();
            point.setXValue(Double.valueOf(xValues[i]));
            point.setYValue(Double.valueOf(yValues[i]));
            point.setFunction(function);
            functionStore.savePoint(function.getId(), point);

            // Логируем каждые 100 точек
            if (i % 100 == 0 || i == xValues.length - 1) {
                logger.debug("Создана точка {}: x={}, y={}", i, xValues[i], yValues[i]);
            }
        }

        logger.info("Сгенерировано {} точек для функции {}", pointsCount, function.getId());
        return new FunctionCreationResult(function, xValues, yValues);
    }

    public EvaluateResponse evaluateFunction(Long functionId, Double x) {
        logger.debug("Вычисление значения функции {} в точке x={}", functionId, x);

        Optional<Function> functionOpt = functionStore.findFunction(functionId);
        if (!functionOpt.isPresent()) {
            logger.warn("Функция с ID {} не найдена", functionId);
            return null;
        }

        Function function = functionOpt.get();

        // Если это табулированная функция, ищем ближайшую точку или интерполируем
        if ("tabulated".equals(function.getSignature()) ||
                "math_function_tabulated".equals(function.getSignature())) {

            List<Point> points = functionStore.findPointsByFunctionId(functionId);
            points.sort(Comparator.comparing(Point::getXValue));

            if (points.isEmpty()) {
                logger.warn("У функции {} нет точек", functionId);
                return null;
            }

            // Если x меньше минимального или больше максимального значения
            if (x < points.get(0).getXValue()) {
                logger.debug("x={} меньше минимального значения {}", x, points.get(0).getXValue());
                return new EvaluateResponse(x, points.get(0).getYValue(), function.getName(), functionId);
            }

            if (x > points.get(points.size() - 1).getXValue()) {
                logger.debug("x={} больше максимального значения {}", x, points.get(points.size() - 1).getXValue());
                return new EvaluateResponse(x, points.get(points.size() - 1).getYValue(), function.getName(), functionId);
            }

            // Поиск интервала для интерполяции
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);

                if (x >= p1.getXValue() && x <= p2.getXValue()) {
                    // Линейная интерполяция
                    double ratio = (x - p1.getXValue()) / (p2.getXValue() - p1.getXValue());
                    double y = p1.getYValue() + ratio * (p2.getYValue() - p1.getYValue());

                    logger.debug("Интерполяция: x={} между [{}, {}], y={}",
                            (Object) x, (Object) p1.getXValue(), (Object) p2.getXValue(), (Object) y);

                    return new EvaluateResponse(x, y, function.getName(), functionId);
                }
            }

            // Точечное совпадение
            for (Point point : points) {
                if (Math.abs(point.getXValue() - x) < 1e-10) {
                    logger.debug("Точное совпадение: x={}, y={}", x, point.getYValue());
                    return new EvaluateResponse(x, point.getYValue(), function.getName(), functionId);
                }
            }
        }

        logger.warn("Не удалось вычислить значение функции {} в точке {}", functionId, x);
        return null;
    }

    public EvaluateTabulatedResponse evaluateTabulated(EvaluateTabulatedRequest request) {
        if (request == null || request.getFunction() == null) {
            throw new IllegalArgumentException("Функция для вычисления не задана");
        }
        if (request.getX() == null) {
            throw new IllegalArgumentException("Значение X должно быть указано");
        }

        TabulatedFunctionPayload payload = request.getFunction();
        TabulatedFunctionFactory chosenFactory = factoryHolder.resolveFactory(request.getFactoryType());
        TabulatedFunction tabulatedFunction = chosenFactory.create(payload.getXValues(), payload.getYValues());

        double y = tabulatedFunction.apply(request.getX());
        EvaluateTabulatedResponse response = new EvaluateTabulatedResponse();
        response.setX(request.getX());
        response.setY(Double.valueOf(y));
        response.setName(payload.getName());
        return response;
    }

    private double calculateMathFunction(String functionKey, double x) {
        MathFunction function = mathFunctionService.getFunctionByKey(functionKey);
        if (function == null) {
            logger.warn("Неизвестная функция: {}", functionKey);
            return 0;
        }
        return function.apply(x);
    }

    public Optional<Function> getFunctionById(Long id) {
        logger.debug("Поиск функции по ID: {}", id);
        return functionStore.findFunction(id);
    }

    public List<Function> getFunctionsByUserId(Long userId) {
        logger.debug("Поиск функций пользователя с ID: {}", userId);
        return functionStore.findFunctionsByUserId(userId);
    }

    public List<Function> getFunctionsByName(String name) {
        logger.debug("Поиск функций по имени: {}", name);
        return functionStore.findAllFunctions().stream()
                .filter(f -> f.getName() != null && f.getName().contains(name))
                .collect(Collectors.toList());
    }

    public List<Function> getAllFunctions() {
        logger.debug("Получение всех функций");
        return functionStore.findAllFunctions();
    }

    public Function updateFunction(Long functionId, Long userId, String name, String signature) {
        logger.info("Обновление функции с ID: {}", functionId);

        Optional<Function> existingFunction = functionStore.findFunction(functionId);
        if (existingFunction.isPresent()) {
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                logger.error("Пользователь с ID {} не существует1", userId);
                return null;
            }

            Function function = existingFunction.get();
            function.setUser(user.get());
            function.setName(name);
            function.setSignature(signature);

            functionStore.saveFunction(function);
            logger.info("Функция с ID {} успешно обновлена", functionId);
            return function;
        }

        logger.warn("Функция с ID {} не найдена для обновления", functionId);
        return null;
    }

    public boolean deleteFunction(Long functionId) {
        logger.info("Удаление функции с ID: {}", functionId);

        boolean removed = functionStore.deleteFunction(functionId);
        if (removed) {
            logger.info("Функция с ID {} и все её точки удалены (in-memory)", functionId);
            return true;
        }

        logger.warn("Функция с ID {} не найдена для удаления", functionId);
        return false;
    }

    public FunctionStatistics getFunctionStatistics(Long functionId) {
        logger.debug("Получение статистики для функции с ID: {}", functionId);

        Optional<Function> function = functionStore.findFunction(functionId);
        if (function.isPresent()) {
            List<Point> points = new ArrayList<>(functionStore.findPointsByFunctionId(functionId));

            double minX = points.stream().mapToDouble(Point::getXValue).min().orElse(0);
            double maxX = points.stream().mapToDouble(Point::getXValue).max().orElse(0);
            double minY = points.stream().mapToDouble(Point::getYValue).min().orElse(0);
            double maxY = points.stream().mapToDouble(Point::getYValue).max().orElse(0);
            double avgY = points.stream().mapToDouble(Point::getYValue).average().orElse(0);

            FunctionStatistics stats = new FunctionStatistics(
                    functionId,
                    function.get().getName(),
                    points.size(),
                    minX,
                    maxX,
                    minY,
                    maxY,
                    avgY
            );

            logger.info("Статистика функции {}: {} точек, x=[{}, {}], y=[{}, {}]",
                    (Object) function.get().getName(), (Object) points.size(), (Object) minX, (Object) maxX, (Object) minY, (Object) maxY);

            return stats;
        }

        logger.warn("Функция с ID {} не найдена для статистики", functionId);
        return null;
    }

    public static class FunctionStatistics {
        private final Long functionId;
        private final String functionName;
        private final int pointCount;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double averageY;

        public FunctionStatistics(Long functionId, String functionName, int pointCount,
                                  double minX, double maxX, double minY, double maxY, double averageY) {
            this.functionId = functionId;
            this.functionName = functionName;
            this.pointCount = pointCount;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.averageY = averageY;
        }

        public Long getFunctionId() { return functionId; }
        public String getFunctionName() { return functionName; }
        public int getPointCount() { return pointCount; }
        public double getMinX() { return minX; }
        public double getMaxX() { return maxX; }
        public double getMinY() { return minY; }
        public double getMaxY() { return maxY; }
        public double getAverageY() { return averageY; }

        @Override
        public String toString() {
            return String.format(
                    "FunctionStatistics{function='%s', points=%d, x=[%.2f, %.2f], y=[%.2f, %.2f], avgY=%.2f}",
                    (Object) functionName, (Object) pointCount, (Object) minX, (Object) maxX, (Object) minY, (Object) maxY, (Object) averageY
            );
        }
    }
}
