package com.mreblan.auth.security;

import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IRevokeService;
import com.mreblan.auth.services.impl.UserServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final IJwtService jwtService;
    private final UserServiceImpl userService;
    private final IRevokeService  revokeService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

        // Получаем заголовок с авторизацией
        String header = request.getHeader("Authorization");
        log.info("HEADER: {}", header);
        // Готовим переменные для проверки
        String username = null;

        // Проверяем, есть ли заголовок
        // и начинается ли он с Bearer
        if (header != null && header.startsWith("Bearer ")) {
            // Получаем токен
            String jwt = header.substring(7);
            jwt = jwt.trim();
            jwt = jwt.replaceAll("[\\s]", "");
            log.info("JWT TOKEN: {}", jwt);

            log.info("TOKEN CONTAINS SPACE: {}", jwt.contains(" "));

            try {
                // Пытаемся распарсить токен
                log.info("PARSING JWT");
                username = jwtService.getUsernameFromJwt(jwt);

                // Если есть имя пользователя,
                // токен валиден
                // и токен не отменён (не найден в Redis)
                if (
                    username != null && 
                    jwtService.isTokenValid(jwt) &&
                    !revokeService.isTokenRevoked(jwt)
                ) {
                    log.info("USERNAME != NULL");
                    // Ищем пользователя с таким именем
                    var user = userService.loadUserByUsername(username);
                    // Добавляем его в контекст
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException e) {
                // Если мы поймали исключение,
                // то говорим, что пользователь не авторизован
                log.error("JWT EXCEPTION");
                log.error(e.getMessage());

                log.error("UNATHORIZED");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        log.info("NEXT FILTERS");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("SHOULD NOT FILTER");
        return (
        new AntPathMatcher().match("/swagger-ui/**", request.getServletPath()) ||
        new AntPathMatcher().match("/v3/api-docs/**", request.getServletPath()) //||
        // new AntPathMatcher().match("/test/**", request.getServletPath()) 
        );
    }
}
