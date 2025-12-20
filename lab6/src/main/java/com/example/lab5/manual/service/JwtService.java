package com.example.lab5.manual.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Простейший сервис для работы с JWT.
 */
public class JwtService {
    private static final String ISSUER = "lab5-manual";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
    }

    public String issueToken(String login, String role) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withClaim("role", role)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(3600)))
                .sign(algorithm);
    }

    public Optional<UserPrincipal> verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(new UserPrincipal(jwt.getSubject(), jwt.getClaim("role").asString()));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public static class UserPrincipal {
        private final String login;
        private final String role;

        public UserPrincipal(String login, String role) {
            this.login = login;
            this.role = role;
        }

        public String getLogin() {
            return login;
        }

        public String getRole() {
            return role;
        }
    }
}
