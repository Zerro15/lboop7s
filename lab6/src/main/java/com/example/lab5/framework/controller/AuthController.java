package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.AuthRequest;
import com.example.lab5.framework.dto.AuthResponse;
import com.example.lab5.framework.dto.CreateUserRequest;
import com.example.lab5.framework.dto.UserDTO;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(
                    request.getLogin(),
                    request.getRole(),
                    request.getPassword()
            );

            UserDTO response = new UserDTO();
            response.setId(user.getId());
            response.setLogin(user.getLogin());
            response.setRole(user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );

            if (!authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.getUserByLogin(request.getLogin())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            String token = "Basic " + Base64.getEncoder()
                    .encodeToString((request.getLogin() + ":" + request.getPassword())
                            .getBytes(StandardCharsets.UTF_8));

            AuthResponse response = new AuthResponse();
            response.setId(user.getId());
            response.setLogin(user.getLogin());
            response.setRole(user.getRole());
            response.setToken(token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}