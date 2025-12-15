package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.exception.AuthenticationException;
import com.example.lab5.manual.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Long createUser(String login, String role, String password) {
        logger.info("Создание пользователя: login={}, role={}", login, role);
        validateCredentials(login, password);
        UserDTO user = new UserDTO(login.trim(), role, encodeIfNeeded(password));
        return userDAO.createUser(user);
    }

    public Optional<UserDTO> getUserById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        return userDAO.findById(id);
    }

    public Optional<UserDTO> getUserByLogin(String login) {
        logger.debug("Поиск пользователя по логину: {}", login);
        return userDAO.findByLogin(login);
    }

    public List<UserDTO> getAllUsers() {
        logger.debug("Получение всех пользователей");
        return userDAO.findAll();
    }

    public List<UserDTO> getUsersByRole(String role) {
        logger.debug("Поиск пользователей по роли: {}", role);
        return userDAO.findByRole(role);
    }

    public boolean updateUser(Long id, String login, String role, String password) {
        logger.info("Обновление пользователя с ID: {}", id);
        Optional<UserDTO> existingUser = userDAO.findById(id);
        if (existingUser.isPresent()) {
            UserDTO user = existingUser.get();
            user.setLogin(login);
            user.setRole(role);
            user.setPassword(encodeIfNeeded(password));
            return userDAO.updateUser(user);
        }
        logger.warn("Пользователь с ID {} не найден для обновления", id);
        return false;
    }

    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        return userDAO.deleteUser(id);
    }

    public UserDTO authenticate(String login, String password) {
        logger.info("Попытка входа для логина {}", login);
        validateCredentials(login, password);

        Optional<UserDTO> user = userDAO.findByLogin(login);
        if (user.isEmpty() || !PasswordService.matches(password, user.get().getPassword())) {
            throw new AuthenticationException("Неверный логин или пароль");
        }
        return user.get();
    }

    public UserDTO register(String login, String password) {
        logger.info("Регистрация пользователя с логином {}", login);
        validateCredentials(login, password);
        if (password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 6 символов");
        }

        Optional<UserDTO> existing = userDAO.findByLogin(login.trim());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }

        UserDTO user = new UserDTO(login.trim(), "USER", PasswordService.hash(password));
        Long id = userDAO.createUser(user);
        user.setId(id);
        return user;
    }

    private void validateCredentials(String login, String password) {
        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Логин и пароль обязательны");
        }
    }

    private String encodeIfNeeded(String password) {
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password;
        }
        return PasswordService.hash(password);
    }
}
