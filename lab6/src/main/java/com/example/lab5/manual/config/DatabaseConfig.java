package com.example.lab5.manual.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();
    private static final HikariDataSource dataSource;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("manual/application.properties")) {
            if (input == null) {
                logger.error("Не найден файл конфигурации manual/application.properties");
                throw new RuntimeException("Configuration file not found");
            }
            properties.load(input);
            logger.info("Конфигурация базы данных загружена");
        } catch (IOException e) {
            logger.error("Ошибка загрузки конфигурации", e);
            throw new RuntimeException("Error loading configuration", e);
        }

        dataSource = initializeDataSource();
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
        String username = firstNonBlank(System.getenv("DB_USERNAME"), properties.getProperty("database.username"), "postgres");
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), properties.getProperty("database.password"), "password");
        String driver = firstNonBlank(System.getenv("DB_DRIVER"), properties.getProperty("database.driver"), "org.postgresql.Driver");

        logger.info("Настройка подключения к базе данных: url={}, user={}", url, username);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("JDBC драйвер не найден: {}", driver, e);
            throw new RuntimeException("JDBC driver not found", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(10_000);
        config.setValidationTimeout(5_000);
        config.setInitializationFailTimeout(10_000);
        config.setPoolName("manual-ui-pool");
        config.setLeakDetectionThreshold(15_000);

        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Не удалось инициализировать пул подключений к БД", e);
            throw new IllegalStateException("Конфигурация базы данных недействительна: " + e.getMessage(), e);
        }
    }

    private static String resolveJdbcUrl() {
        String explicitUrl = System.getenv("DB_URL");
        if (explicitUrl != null && !explicitUrl.isBlank()) {
            return explicitUrl;
        }
        explicitUrl = properties.getProperty("database.url");
        if (explicitUrl != null && !explicitUrl.isBlank()) {
            return explicitUrl;
        }

        String host = firstNonBlank(System.getenv("DB_HOST"), properties.getProperty("database.host"), "postgres");
        String port = firstNonBlank(System.getenv("DB_PORT"), properties.getProperty("database.port"), "5432");
        String db = firstNonBlank(System.getenv("DB_NAME"), properties.getProperty("database.name"), "lab5db");
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
