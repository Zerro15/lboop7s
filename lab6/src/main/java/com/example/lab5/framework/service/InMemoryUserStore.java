package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStore {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserStore.class);

    private final Map<String, User> usersByLogin = new ConcurrentHashMap<>();
    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public synchronized User save(User user) {
        if (user.getId() == null) {
            user.setId(idSequence.getAndIncrement());
        }

        usersByLogin.put(user.getLogin(), user);
        usersById.put(user.getId(), user);

        logger.debug("Пользователь {} сохранен в памяти (id={})", user.getLogin(), user.getId());
        return user;
    }

    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(usersByLogin.get(login));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public boolean existsByLogin(String login) {
        return usersByLogin.containsKey(login);
    }

    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    public void deleteById(Long id) {
        User removed = usersById.remove(id);
        if (removed != null) {
            usersByLogin.remove(removed.getLogin());
            logger.debug("Пользователь {} удален из памяти", removed.getLogin());
        }
    }

    public void clear() {
        usersByLogin.clear();
        usersById.clear();
        logger.debug("Очищено все содержимое InMemoryUserStore");
    }
}
