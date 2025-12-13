package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.logging.SecurityLogger;
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

    public List<UserDTO> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userDAO.findById(id);
    }

    public Optional<UserDTO> getUserByLogin(String login) {
        return userDAO.findByLogin(login);
    }

    public List<UserDTO> getUsersByRole(String role) {
        return userDAO.findByRole(role);
    }

    public Long createUser(String login, String role, String password) {
        UserDTO user = new UserDTO();
        user.setLogin(login);
        user.setRole(role);
        user.setPassword(password);
        Long id = userDAO.createUser(user);
        SecurityLogger.logUserCreation("system", login, role);
        return id;
    }

    public boolean updateUser(Long id, String login, String role, String password) {
        UserDTO existing = new UserDTO();
        existing.setId(id);
        existing.setLogin(login);
        existing.setRole(role);
        existing.setPassword(password);
        boolean updated = userDAO.updateUser(existing);
        if (updated) {
            logger.info("User {} updated", id);
        }
        return updated;
    }

    public boolean deleteUser(Long id) {
        Optional<UserDTO> target = userDAO.findById(id);
        target.ifPresent(user -> SecurityLogger.logUserDeletion("system", user.getLogin()));
        return userDAO.deleteUser(id);
    }
}
