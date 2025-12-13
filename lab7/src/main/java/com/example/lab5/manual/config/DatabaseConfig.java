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

public final class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String PROPERTIES_FILE = "/application.properties";
    private static HikariDataSource dataSource;

    static {
        initDataSource();
    }

    private DatabaseConfig() {
    }

    private static void initDataSource() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.warn("application.properties not found in classpath; using defaults");
            }
        } catch (IOException e) {
            logger.error("Failed to load database properties", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/lab5_db"));
        config.setUsername(properties.getProperty("db.username", "lab5_user"));
        config.setPassword(properties.getProperty("db.password", "lab5_password"));
        config.setDriverClassName(properties.getProperty("db.driver-class", "org.postgresql.Driver"));

        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maximum-pool-size", "10")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minimum-idle", "2")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idle-timeout", "300000")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connection-timeout", "20000")));
        config.setMaxLifetime(Long.parseLong(properties.getProperty("db.pool.max-lifetime", "1200000")));
        config.setLeakDetectionThreshold(Long.parseLong(properties.getProperty("db.pool.leak-detection-threshold", "60000")));

        dataSource = new HikariDataSource(config);
        logger.info("HikariCP initialized for URL: {}", config.getJdbcUrl());
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
