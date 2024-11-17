package com.mreblan.auth.security;

import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.impl.UserServiceImpl;
import com.mreblan.auth.services.impl.JwtServiceImpl;

import com.mreblan.auth.entities.User;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private IJwtService jwtService;
    // private IAuthenticationService authService;
    private UserServiceImpl userService;

    @Autowired
    public void setJwtService(JwtServiceImpl jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");
        log.info("HEADER: {}", header);
        String username = null;

        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            log.info("JWT TOKEN: {}", jwt);

            try {
                username = jwtService.getUsernameFromJwt(jwt);

                if (username != null && jwtService.isTokenValid(jwt)) {
                    log.info("USERNAME != NULL");
                    var user = userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                log.error("JWT EXCEPTION");
                e.printStackTrace();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
