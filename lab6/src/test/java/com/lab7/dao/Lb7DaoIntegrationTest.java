package com.lab7.dao;

import com.lab7.Lab7Application;
import com.lab7.entity.Function;
import com.lab7.entity.Points;
import com.lab7.entity.User;
import com.lab7.enums.FunctionType;
import com.lab7.enums.UserRole;
import com.lab7.util.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class Lb7DaoIntegrationTest {

    private Connection connection;
    private UserDAO userDAO;
    private FunctionDAO functionDAO;
    private PointsDAO pointsDAO;

    @BeforeAll
    static void initSchema() {
        System.setProperty("DB_URL", "jdbc:h2:mem:lb7_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        System.setProperty("DB_USER", "sa");
        System.setProperty("DB_PASSWORD", "");
        Lab7Application.initialize();
    }

    @BeforeEach
    void setUp() throws Exception {
        connection = Database.getConnection();
        userDAO = new UserDAO(connection);
        functionDAO = new FunctionDAO(connection);
        pointsDAO = new PointsDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void userFunctionAndPointsLifecycle() throws Exception {
        User user = new User(null, "lb7_user", BCrypt.hashpw("password", BCrypt.gensalt()), "user@example.com", UserRole.ADMIN, null);
        Long userId = userDAO.create(user);
        assertNotNull(userId);

        User storedUser = userDAO.findUsername("lb7_user");
        assertNotNull(storedUser);
        assertEquals(UserRole.ADMIN, storedUser.getRole());

        Function function = new Function(null, "demo", FunctionType.SQR, null, userId);
        Long functionId = functionDAO.create(function);
        assertNotNull(functionId);

        Function storedFunction = functionDAO.findId(functionId);
        assertNotNull(storedFunction);
        assertEquals(function.getName(), storedFunction.getName());

        Points points = new Points(null, new Double[]{0.0, 1.0, 2.0}, new Double[]{0.0, 1.0, 4.0}, functionId);
        Long pointsId = pointsDAO.create(points);
        assertNotNull(pointsId);

        Points storedPoints = pointsDAO.findId(pointsId);
        assertNotNull(storedPoints);
        assertEquals(functionId, storedPoints.getFunctionId());
        assertArrayEquals(points.getXValues(), storedPoints.getXValues());
        assertArrayEquals(points.getYValues(), storedPoints.getYValues());
    }
}
