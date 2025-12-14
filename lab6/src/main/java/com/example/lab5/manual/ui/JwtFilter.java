package com.example.lab5.manual.ui;

import com.example.lab5.manual.service.JwtService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр, требующий наличия валидного JWT для всех API кроме аутентификации.
 */
@WebFilter("/ui/api/*")
public class JwtFilter implements Filter {
    private final JwtService jwtService = new JwtService("manual-secret-key");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (path.contains("/ui/api/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = header.substring("Bearer ".length());
        Optional<JwtService.UserPrincipal> principal = jwtService.verify(token);
        if (principal.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        req.setAttribute("principal", principal.get());
        chain.doFilter(request, response);
    }
}
