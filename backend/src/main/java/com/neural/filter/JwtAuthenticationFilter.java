package com.neural.filter;

import com.neural.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("Filter executing - path: {}", path);

        // 允许的路径（无需认证）
        if (path.startsWith("/api/v1/auth/login") || path.startsWith("/v1/auth/login") ||
                path.startsWith("/api/v1/auth/register") || path.startsWith("/v1/auth/register") ||
                path.startsWith("/api/v1/upload") || path.startsWith("/v1/upload") ||
                path.startsWith("/api/v1/hello") || path.startsWith("/v1/hello")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查 Authorization Header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Header empty or format incorrect");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return;
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                log.debug("Token validation failed");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\"}");
                return;
            }

            // 在 token 中提取用户信息并存入 request attributes
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);

            log.debug("Token parsed successfully - userId: {}, role: {}, username: {}", userId, role, username);

            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            request.setAttribute("username", username);

        } catch (Throwable e) {
            log.error("Severe error during token processing: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"认证服务异常: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
