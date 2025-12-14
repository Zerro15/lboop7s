package com.example.lab5.manual.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простое хранилище пользователей для JWT-авторизации.
 */
public class InMemoryUserStore {
    private final Map<String, String> passwordByLogin = new ConcurrentHashMap<>();
    private final Map<String, String> roleByLogin = new ConcurrentHashMap<>();

    public InMemoryUserStore() {
        passwordByLogin.put("admin", "admin");
        roleByLogin.put("admin", "ADMIN");
    }

    public synchronized boolean register(String login, String password) {
        if (passwordByLogin.containsKey(login)) {
            return false;
        }
        passwordByLogin.put(login, password);
        roleByLogin.put(login, "USER");
        return true;
    }

    public Optional<String> getPassword(String login) {
        return Optional.ofNullable(passwordByLogin.get(login));
    }

    public Optional<String> getRole(String login) {
        return Optional.ofNullable(roleByLogin.get(login));
    }
}
