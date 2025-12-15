package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FrameworkPerformanceComparisonService {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkPerformanceComparisonService.class);

    @Autowired
    public UserService userService;

    @Autowired
    public FunctionService functionService;

    @Autowired
    public PointService pointService;

    // Храним ссылки на тестовые данные для очистки
    private List<User> testUsers = new ArrayList<>();
    private List<Function> testFunctions = new ArrayList<>();

    public void generateTestData(int userCount, int functionsPerUser, int pointsPerFunction) {
        logger.info("Генерация тестовых данных для Framework: {} пользователей, {} функций на пользователя, {} точек на функцию"
        );

        testUsers.clear();
        testFunctions.clear();
        int totalPoints = 0;

        for (int i = 0; i < userCount; i++) {
            String login = "framework_perf_user_" + System.currentTimeMillis() + "_" + i;
            var user = userService.createUser(login, "USER", "password123");
            testUsers.add(user);

            for (int j = 0; j < functionsPerUser; j++) {
                String functionName = "framework_func_" + i + "_" + j;
                String expression = "f(x) = x^" + j;
                var function = functionService.createFunction(user.getId(), functionName, expression);
                testFunctions.add(function);

                int pointsGenerated = pointService.generateFunctionPoints(function.getId(), "polynomial", -10, 10, 0.1);
                totalPoints += pointsGenerated;
            }
        }

        logger.info("Framework: Сгенерировано: {} пользователей, {} функций, {} точек"
        );
    }

    public PerformanceResults comparePerformance() {
        // Проверяем что база не пустая
        long userCount = userService.getAllUsers().size();
        long functionCount = functionService.getAllFunctions().size();


        if (userCount < 50) {
            System.out.println("ПРЕДУПРЕЖДЕНИЕ: База данных может быть недостаточно заполнена для реалистичного тестирования!");
        }

        PerformanceResults results = new PerformanceResults();

        testUserOperations(results);
        testFunctionOperations(results);
        testPointOperations(results);
        testComplexQueries(results);

        return results;
    }

    private void testUserOperations(PerformanceResults results) {
        long startTime, endTime;

        // Create User - время создания ОДНОГО пользователя НА ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        String login = "framework_perf_test_" + System.currentTimeMillis();
        User user = userService.createUser(login, "ADMIN", "testpass");
        endTime = System.nanoTime();
        results.setUserCreateTime((endTime - startTime) / 1_000_000.0);

        // Read User - время чтения СУЩЕСТВУЮЩЕГО пользователя ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        User existingUser = testUsers.get(0);
        startTime = System.nanoTime();
        userService.getUserById(existingUser.getId());
        endTime = System.nanoTime();
        results.setUserReadTime((endTime - startTime) / 1_000_000.0);

        // Update User - время обновления СУЩЕСТВУЮЩЕГО пользователя В ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        userService.updateUser(existingUser.getId(), existingUser.getLogin() + "_updated", "USER", "newpass");
        endTime = System.nanoTime();
        results.setUserUpdateTime((endTime - startTime) / 1_000_000.0);

        // Delete User - время удаления НОВОГО пользователя ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        userService.deleteUser(user.getId());
        endTime = System.nanoTime();
        results.setUserDeleteTime((endTime - startTime) / 1_000_000.0);
    }

    private void testFunctionOperations(PerformanceResults results) {
        long startTime, endTime;

        // Используем существующего пользователя из тестовых данных
        User existingUser = testUsers.get(0);

        // Create Function - время создания ОДНОЙ функции НА ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        Function function = functionService.createFunction(existingUser.getId(), "test_function_perf", "f(x) = x^2");
        endTime = System.nanoTime();
        results.setFunctionCreateTime((endTime - startTime) / 1_000_000.0);

        // Read Function - время чтения ОДНОЙ функции ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        functionService.getFunctionById(function.getId());
        endTime = System.nanoTime();
        results.setFunctionReadTime((endTime - startTime) / 1_000_000.0);

        // Удаляем тестовую функцию
        functionService.deleteFunction(function.getId());
    }

    private void testPointOperations(PerformanceResults results) {
        long startTime, endTime;

        // Используем существующую функцию из тестовых данных
        Function existingFunction = testFunctions.get(0);

        // Create Points - время создания точек ДЛЯ СУЩЕСТВУЮЩЕЙ ФУНКЦИИ
        startTime = System.nanoTime();
        int pointCount = pointService.generateFunctionPoints(existingFunction.getId(), "linear", -5, 5, 1);
        endTime = System.nanoTime();
        results.setPointCreateTime((endTime - startTime) / 1_000_000.0);

        // Read Points - время чтения точек ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        pointService.getPointsByFunctionId(existingFunction.getId());
        endTime = System.nanoTime();
        results.setPointReadTime((endTime - startTime) / 1_000_000.0);
    }

    private void testComplexQueries(PerformanceResults results) {
        long startTime, endTime;

        // Complex Query - время выполнения сложных запросов НА РЕАЛЬНЫХ ДАННЫХ
        startTime = System.nanoTime();

        // Запрос 1: Получить всех пользователей
        var allUsers = userService.getAllUsers();

        // Запрос 2: Для каждого пользователя получить все функции
        for (var user : allUsers) {
            var userFunctions = functionService.getFunctionsByUserId(user.getId());

            // Запрос 3: Для каждой функции получить точки (только для первых 3 функций)
            for (int i = 0; i < Math.min(userFunctions.size(), 3); i++) {
                pointService.getPointsByFunctionId(userFunctions.get(i).getId());
            }
        }

        endTime = System.nanoTime();
        results.setComplexQueryTime((endTime - startTime) / 1_000_000.0);
    }

    // Метод для очистки тестовых данных
    public void cleanupTestData() {
        logger.info("Очистка тестовых данных Framework: {} пользователей", Optional.of(testUsers.size()));
        for (User user : testUsers) {
            userService.deleteUser(user.getId());
        }
        testUsers.clear();
        testFunctions.clear();
    }

    // Метод для получения всех функций (для проверки)
    public List<Function> getAllFunctions() {
        return functionService.getAllFunctions();
    }

    public static class PerformanceResults {
        private double userCreateTime;
        private double userReadTime;
        private double userUpdateTime;
        private double userDeleteTime;
        private double functionCreateTime;
        private double functionReadTime;
        private double pointCreateTime;
        private double pointReadTime;
        private double complexQueryTime;
        private Date testDate = new Date();
        private int testDataSize = 100; // Примерное количество записей в базе

        // Геттеры и сеттеры
        public double getUserCreateTime() {
            return userCreateTime;
        }

        public void setUserCreateTime(double userCreateTime) {
            this.userCreateTime = userCreateTime;
        }

        public double getUserReadTime() {
            return userReadTime;
        }

        public void setUserReadTime(double userReadTime) {
            this.userReadTime = userReadTime;
        }

        public double getUserUpdateTime() {
            return userUpdateTime;
        }

        public void setUserUpdateTime(double userUpdateTime) {
            this.userUpdateTime = userUpdateTime;
        }

        public double getUserDeleteTime() {
            return userDeleteTime;
        }

        public void setUserDeleteTime(double userDeleteTime) {
            this.userDeleteTime = userDeleteTime;
        }

        public double getFunctionCreateTime() {
            return functionCreateTime;
        }

        public void setFunctionCreateTime(double functionCreateTime) {
            this.functionCreateTime = functionCreateTime;
        }

        public double getFunctionReadTime() {
            return functionReadTime;
        }

        public void setFunctionReadTime(double functionReadTime) {
            this.functionReadTime = functionReadTime;
        }

        public double getPointCreateTime() {
            return pointCreateTime;
        }

        public void setPointCreateTime(double pointCreateTime) {
            this.pointCreateTime = pointCreateTime;
        }

        public double getPointReadTime() {
            return pointReadTime;
        }

        public void setPointReadTime(double pointReadTime) {
            this.pointReadTime = pointReadTime;
        }

        public double getComplexQueryTime() {
            return complexQueryTime;
        }

        public void setComplexQueryTime(double complexQueryTime) {
            this.complexQueryTime = complexQueryTime;
        }

        public Date getTestDate() {
            return testDate;
        }

        public void setTestDate(Date testDate) {
            this.testDate = testDate;
        }

        public int getTestDataSize() {
            return testDataSize;
        }

        public void setTestDataSize(int testDataSize) {
            this.testDataSize = testDataSize;
        }

        // ДОБАВЛЕННЫЕ МЕТОДЫ:

        /**
         * Форматирует результаты в виде Markdown таблицы
         */
        public String toMarkdownTable() {
            double totalTime = calculateTotalTime();

            return String.format(
                    "# Результаты тестирования производительности Spring Data JPA\n\n" +
                            "**Дата теста:** %s\n" +
                            "**Размер тестовых данных:** ~%d записей\n\n" +
                            "## Время выполнения операций (мс)\n" +
                            "| Операция | Время (мс) | Описание |\n" +
                            "|----------|------------|----------|\n" +
                            "| Создание пользователя | %.3f | Среднее время создания одного пользователя |\n" +
                            "| Чтение пользователя | %.3f | Среднее время чтения одного пользователя |\n" +
                            "| Обновление пользователя | %.3f | Среднее время обновления одного пользователя |\n" +
                            "| Удаление пользователя | %.3f | Среднее время удаления одного пользователя |\n" +
                            "| Создание функции | %.3f | Среднее время создания одной функции |\n" +
                            "| Чтение функции | %.3f | Среднее время чтения одной функции |\n" +
                            "| Создание точек | %.3f | Среднее время создания серии точек |\n" +
                            "| Чтение точек | %.3f | Среднее время чтения точек функции |\n" +
                            "| Сложные запросы | %.3f | Время выполнения сложных связанных запросов |\n" +
                            "| **Общее время** | **%.3f** | **Суммарное время всех операций** |\n\n" +
                            "## Сводка производительности\n" +
                            "- **Всего операций:** %d\n" +
                            "- **Общее время:** %.3f мс\n" +
                            "- **Среднее время операции:** %.3f мс\n",

                    testDate,
                    testDataSize,
                    userCreateTime,
                    userReadTime,
                    userUpdateTime,
                    userDeleteTime,
                    functionCreateTime,
                    functionReadTime,
                    pointCreateTime,
                    pointReadTime,
                    complexQueryTime,
                    totalTime,
                    getOperationCount(),
                    totalTime,
                    totalTime / getOperationCount()
            );
        }

        /**
         * Форматирует результаты в CSV формат
         */
        public String toCSV() {
            double totalTime = calculateTotalTime();

            return String.format(
                    "Дата теста,%s\n" +
                            "Размер тестовых данных,%d\n" +
                            "Создание пользователя (мс),%.3f\n" +
                            "Чтение пользователя (мс),%.3f\n" +
                            "Обновление пользователя (мс),%.3f\n" +
                            "Удаление пользователя (мс),%.3f\n" +
                            "Создание функции (мс),%.3f\n" +
                            "Чтение функции (мс),%.3f\n" +
                            "Создание точек (мс),%.3f\n" +
                            "Чтение точек (мс),%.3f\n" +
                            "Сложные запросы (мс),%.3f\n" +
                            "Общее время (мс),%.3f\n" +
                            "Всего операций,%d\n" +
                            "Среднее время операции (мс),%.3f",

                    testDate,
                    testDataSize,
                    userCreateTime,
                    userReadTime,
                    userUpdateTime,
                    userDeleteTime,
                    functionCreateTime,
                    functionReadTime,
                    pointCreateTime,
                    pointReadTime,
                    complexQueryTime,
                    totalTime,
                    getOperationCount(),
                    totalTime / getOperationCount()
            );
        }

        /**
         * Форматирует результаты в краткую Markdown таблицу (для сравнения)
         */
        public String toShortMarkdownTable() {
            double totalTime = calculateTotalTime();

            return String.format(
                    "# Краткие результаты тестирования\n\n" +
                            "**Дата:** %s  \n" +
                            "**Размер данных:** ~%d записей\n\n" +
                            "| Метрика | Значение |\n" +
                            "|---------|----------|\n" +
                            "| CRUD пользователей (мс) | %.3f |\n" +
                            "| CRUD функций (мс) | %.3f |\n" +
                            "| CRUD точек (мс) | %.3f |\n" +
                            "| Сложные запросы (мс) | %.3f |\n" +
                            "| **Общее время (мс)** | **%.3f** |\n" +
                            "| Операций выполнено | %d |\n" +
                            "| Среднее время/операция (мс) | %.3f |",

                    testDate,
                    testDataSize,
                    userCreateTime + userReadTime + userUpdateTime + userDeleteTime,
                    functionCreateTime + functionReadTime,
                    pointCreateTime + pointReadTime,
                    complexQueryTime,
                    totalTime,
                    getOperationCount(),
                    totalTime / getOperationCount()
            );
        }

        /**
         * Рассчитывает общее время всех операций
         */
        private double calculateTotalTime() {
            return userCreateTime + userReadTime + userUpdateTime + userDeleteTime +
                    functionCreateTime + functionReadTime +
                    pointCreateTime + pointReadTime +
                    complexQueryTime;
        }

        /**
         * Возвращает общее количество выполненных операций
         */
        private int getOperationCount() {
            // Каждая операция измерялась по одному разу
            return 9; // userCreate, userRead, userUpdate, userDelete,
            // functionCreate, functionRead, pointCreate, pointRead, complexQuery
        }

        /**
         * Генерирует JSON представление результатов
         */
        public String toJson() {
            double totalTime = calculateTotalTime();

            return String.format(
                    "{\n" +
                            "  \"testDate\": \"%s\",\n" +
                            "  \"testDataSize\": %d,\n" +
                            "  \"operations\": {\n" +
                            "    \"userCreate\": %.3f,\n" +
                            "    \"userRead\": %.3f,\n" +
                            "    \"userUpdate\": %.3f,\n" +
                            "    \"userDelete\": %.3f,\n" +
                            "    \"functionCreate\": %.3f,\n" +
                            "    \"functionRead\": %.3f,\n" +
                            "    \"pointCreate\": %.3f,\n" +
                            "    \"pointRead\": %.3f,\n" +
                            "    \"complexQuery\": %.3f\n" +
                            "  },\n" +
                            "  \"summary\": {\n" +
                            "    \"totalTime\": %.3f,\n" +
                            "    \"operationCount\": %d,\n" +
                            "    \"averageTimePerOperation\": %.3f\n" +
                            "  }\n" +
                            "}",

                    testDate,
                    testDataSize,
                    userCreateTime,
                    userReadTime,
                    userUpdateTime,
                    userDeleteTime,
                    functionCreateTime,
                    functionReadTime,
                    pointCreateTime,
                    pointReadTime,
                    complexQueryTime,
                    totalTime,
                    getOperationCount(),
                    totalTime / getOperationCount()
            );
        }

        @Override
        public String toString() {
            return String.format(
                    "PerformanceResults[\n" +
                            "  testDate=%s,\n" +
                            "  userCreateTime=%.3fms,\n" +
                            "  userReadTime=%.3fms,\n" +
                            "  userUpdateTime=%.3fms,\n" +
                            "  userDeleteTime=%.3fms,\n" +
                            "  functionCreateTime=%.3fms,\n" +
                            "  functionReadTime=%.3fms,\n" +
                            "  pointCreateTime=%.3fms,\n" +
                            "  pointReadTime=%.3fms,\n" +
                            "  complexQueryTime=%.3fms\n" +
                            "]",
                    testDate,
                    userCreateTime, userReadTime, userUpdateTime, userDeleteTime,
                    functionCreateTime, functionReadTime,
                    pointCreateTime, pointReadTime,
                    complexQueryTime
            );
        }
    }
}
