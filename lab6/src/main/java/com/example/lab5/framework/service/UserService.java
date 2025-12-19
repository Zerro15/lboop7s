package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lab5.framework.repository.UserRepository;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final InMemoryUserStore inMemoryUserStore;
    private final UserRepository userRepository;

    @Autowired
    public UserService(InMemoryUserStore inMemoryUserStore, UserRepository userRepository) {
        this.inMemoryUserStore = inMemoryUserStore;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void bootstrapUsers() {
        logger.info("Инициализация InMemoryUserStore из базы данных (если доступна)");
        userRepository.findAll().forEach(inMemoryUserStore::save);
        logger.info("В память загружено {} пользователей", inMemoryUserStore.findAll().size());
    }

    @Transactional
    public User createUser(String login, String role, String password) {
        logger.info("Создание пользователя: {}, роль: {}", login, role);

        if (inMemoryUserStore.existsByLogin(login)) {
            logger.warn("Пользователь с логином {} уже существует", login);
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        if (!isValidRole(role)) {
            logger.warn("Недопустимая роль: {}", role);
            throw new IllegalArgumentException("Недопустимая роль. Разрешены: ADMIN, USER");
        }

        // ВАЖНО: Сохраняем пароль как есть, БЕЗ {noop} префикса!
        // Spring Security сам добавит его при проверке
        User user = new User(login, role, password);
        User saved = inMemoryUserStore.save(user);
        // Сохраняем в БД ради совместимости с остальными слоями, но аутентификация идет из памяти
        userRepository.save(saved);

        logger.info("Пользователь создан с ID: {} (пароль сохранен без префикса: {})",
                saved.getId(), password);
        return saved;
    }

    public List<User> getAllUsers() {
        logger.info("Получение всех пользователей");
        return inMemoryUserStore.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        return inMemoryUserStore.findById(id);
    }

    public Optional<User> getUserByLogin(String login) {
        logger.info("Поиск пользователя по логину: {}", login);
        return inMemoryUserStore.findByLogin(login);
    }

    @Transactional
    public User updateUser(Long id, String login, String role, String password) {
        logger.info("Обновление пользователя с ID: {}", id);

        Optional<User> existing = inMemoryUserStore.findById(id);
        if (!existing.isPresent()) {
            return null;
        }

        User user = existing.get();
        user.setLogin(login);
        user.setRole(role);

        if (password != null && !password.isEmpty()) {
            // Обновляем пароль как есть, БЕЗ {noop} префикса
            user.setPassword(password);
            logger.debug("Пароль обновлен для пользователя ID: {}", id);
        }

        if (!isValidRole(role)) {
            logger.warn("Недопустимая роль: {}", role);
            throw new IllegalArgumentException("Недопустимая роль. Разрешены: ADMIN, USER");
        }

        User updated = inMemoryUserStore.save(user);
        userRepository.save(updated);
        logger.info("Пользователь с ID {} обновлен", id);
        return updated;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        Optional<User> existing = inMemoryUserStore.findById(id);
        if (!existing.isPresent()) {
            logger.warn("Пользователь с ID {} не найден для удаления", id);
            return false;
        }

        inMemoryUserStore.deleteById(id);
        userRepository.deleteById(id);
        logger.info("Пользователь с ID {} удален", id);
        return true;
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "USER".equals(role);
    }
}
