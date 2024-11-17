package com.mreblan.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IUserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private IJwtService jwtService;
    private IUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String username = null;
        User user = null;
        UsernamePasswordAuthenticationToken auth = null;

        try {
            String headerAuth = request.getHeader("Authorization");

            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7);
            }
            if (jwt != null) {
                try {
                    username = jwtService.getUsernameFromJwt(jwt);
                } catch (ExpiredJwtException e) {
                    log.error("TOKEN EXPIRED");

                    e.printStackTrace();
                }
            }

                
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                user = (User) userService.loadUserByUsername(username);
                auth = new UsernamePasswordAuthenticationToken(
                    user, 
                    null
                    );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            // TODO
        }
    }
}
