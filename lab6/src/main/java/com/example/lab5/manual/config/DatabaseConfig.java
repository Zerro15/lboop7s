package com.example.lab5.manual.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();
    private static final HikariDataSource dataSource;

    private static final String CLASSPATH_CONFIG = "manual/application.properties";
    private static final String ENV_CONFIG_PATH = "MANUAL_DB_CONFIG";
    private static final String SYS_CONFIG_PATH = "manual.db.config";

    static {
        loadProperties();
        dataSource = initializeDataSource();
        initializeSchemaIfNeeded();
    }

    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Не удалось подключиться к базе данных: {}", dataSource.getJdbcUrl(), e);
            throw new IllegalStateException("Не удалось подключиться к базе данных. Проверьте конфигурацию соединения", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static HikariDataSource initializeDataSource() {
        String url = resolveJdbcUrl();
        String username = firstNonBlank(
                System.getenv("DB_USERNAME"),
                getProperty("db.username", "database.username"),
                "lab5_user");
        String password = firstNonBlank(
                System.getenv("DB_PASSWORD"),
                getProperty("db.password", "database.password"),
                "lab5_password");
        String driver = firstNonBlank(
                System.getenv("DB_DRIVER"),
                getProperty("db.driver-class", "db.driver", "database.driver"),
                "org.postgresql.Driver");

        logger.info("Настройка подключения к базе данных: url={}, user={}", url, username);
        loadDriver(driver);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setPoolName("manual-ui-pool");

        config.setMaximumPoolSize(intProp("db.pool.maximum-pool-size", 10));
        config.setMinimumIdle(intProp("db.pool.minimum-idle", 2));
        config.setIdleTimeout(longProp("db.pool.idle-timeout", 300_000));
        config.setConnectionTimeout(longProp("db.pool.connection-timeout", 20_000));
        config.setMaxLifetime(longProp("db.pool.max-lifetime", 1_200_000));
        config.setLeakDetectionThreshold(longProp("db.pool.leak-detection-threshold", 60_000));
        config.setValidationTimeout(5_000);
        config.setInitializationFailTimeout(10_000);

        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Не удалось инициализировать пул подключений к БД", e);
            throw new IllegalStateException("Конфигурация базы данных недействительна: " + e.getMessage(), e);
        }
    }

    private static void initializeSchemaIfNeeded() {
        String jdbcUrl = dataSource.getJdbcUrl();
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:h2:")) {
            return;
        }

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "login VARCHAR(255) UNIQUE NOT NULL, " +
                    "role VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS functions (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "u_id BIGINT NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "signature TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (u_id) REFERENCES users(id) ON DELETE CASCADE);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS points (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "f_id BIGINT NOT NULL, " +
                    "x DOUBLE PRECISION NOT NULL, " +
                    "y DOUBLE PRECISION NOT NULL, " +
                    "FOREIGN KEY (f_id) REFERENCES functions(id) ON DELETE CASCADE);");

            logger.info("Инициализирована тестовая схема H2 для URL {}", jdbcUrl);
        } catch (SQLException e) {
            logger.error("Не удалось подготовить тестовую схему H2", e);
            throw new IllegalStateException("Не удалось создать тестовую схему БД", e);
        }
    }

    private static void loadProperties() {
        Properties loaded = new Properties();

        // 1) External file via system property or environment variable
        String externalPath = firstNonBlank(System.getProperty(SYS_CONFIG_PATH), System.getenv(ENV_CONFIG_PATH));
        if (externalPath != null) {
            try (InputStream input = Files.newInputStream(Path.of(externalPath))) {
                loaded.load(input);
                logger.info("Конфигурация БД загружена из файла {}", externalPath);
            } catch (IOException e) {
                logger.warn("Не удалось загрузить конфигурацию из {}: {}", externalPath, e.getMessage());
            }
        }

        // 2) Default classpath configuration
        if (loaded.isEmpty()) {
            try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG)) {
                if (input != null) {
                    loaded.load(input);
                    logger.info("Конфигурация БД загружена из classpath: {}", CLASSPATH_CONFIG);
                }
            } catch (IOException e) {
                logger.warn("Ошибка загрузки конфигурации из classpath: {}", e.getMessage());
            }
        }

        // 3) Safe defaults for test execution
        if (loaded.isEmpty()) {
            logger.warn("Конфигурация БД не найдена, используем встроенную H2 для тестов");
            loaded.putAll(defaultTestProperties());
        }

        properties.putAll(loaded);
    }

    private static Properties defaultTestProperties() {
        Properties defaults = new Properties();
        defaults.setProperty("db.url", "jdbc:h2:mem:lab5_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        defaults.setProperty("db.username", "sa");
        defaults.setProperty("db.password", "");
        defaults.setProperty("db.driver-class", "org.h2.Driver");
        defaults.setProperty("db.pool.maximum-pool-size", "5");
        defaults.setProperty("db.pool.minimum-idle", "1");
        defaults.setProperty("db.pool.connection-timeout", "10000");
        return defaults;
    }

    private static String resolveJdbcUrl() {
        String explicitUrl = firstNonBlank(System.getenv("DB_URL"),
                getProperty("db.url", "database.url"));
        if (explicitUrl != null && !explicitUrl.isBlank()) {
            return explicitUrl;
        }

        String host = firstNonBlank(System.getenv("DB_HOST"), getProperty("db.host", "database.host"), "localhost");
        String port = firstNonBlank(System.getenv("DB_PORT"), getProperty("db.port", "database.port"), "5432");
        String db = firstNonBlank(System.getenv("DB_NAME"), getProperty("db.name", "database.name"), "lab5_db");
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
    }

    private static void loadDriver(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("JDBC драйвер не найден: {}", driver, e);
            throw new IllegalStateException("JDBC driver not found: " + driver, e);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String getProperty(String... keys) {
        for (String key : keys) {
            String value = properties.getProperty(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static int intProp(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Некорректное целочисленное значение {} для {}, использую {}", value, key, defaultValue);
            return defaultValue;
        }
    }

    private static long longProp(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.warn("Некорректное числовое значение {} для {}, использую {}", value, key, defaultValue);
            return defaultValue;
        }
    }
}
