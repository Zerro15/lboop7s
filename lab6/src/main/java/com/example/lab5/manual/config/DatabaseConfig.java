package com.example.lab5.manual.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();

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
    }

    public static Connection getConnection() throws SQLException {
        String url = envOrProperty("DB_URL", "database.url");
        String username = envOrProperty("DB_USERNAME", "database.username");
        String password = envOrProperty("DB_PASSWORD", "database.password");
        String driver = envOrProperty("DB_DRIVER", "database.driver");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("JDBC url is not configured");
        }

        try {
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            logger.error("JDBC драйвер не найден: {}", driver, e);
            throw new RuntimeException("JDBC driver not found", e);
        }

        logger.debug("Подключение к базе данных: {}", url);
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.error("Не удалось подключиться к базе данных: {}", url, e);
            throw new IllegalStateException("Не удалось подключиться к базе данных. Проверьте конфигурацию соединения", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    private static String envOrProperty(String envKey, String propertyKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return properties.getProperty(propertyKey);
    }
}
