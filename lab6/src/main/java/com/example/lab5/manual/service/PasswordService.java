package com.example.lab5.manual.service;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Хелпер для безопасного хеширования и проверки паролей.
 */
public class PasswordService {
    private PasswordService() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        // Поддержка старых записей без BCrypt
        if (!storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$") && !storedPassword.startsWith("$2y$")) {
            return storedPassword.equals(rawPassword);
        }

        return BCrypt.checkpw(rawPassword, storedPassword);
    }
}
