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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private IJwtService jwtService;
    // private IAuthenticationService authService;
    private UserServiceImpl userService;

    private final RequestMatcher swaggerHtml = new AntPathRequestMatcher("/swagger-ui/index.html");
    private final RequestMatcher swaggerV3 = new AntPathRequestMatcher("/v3/api-docs");
    private final RequestMatcher testFind = new AntPathRequestMatcher("/test/find");
    private final RequestMatcher testRevoke = new AntPathRequestMatcher("/test/revoke");
    private final RequestMatcher testDelete = new AntPathRequestMatcher("/test/delete");

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

        if (
        this.swaggerHtml.matches(request) || 
        this.swaggerV3.matches(request)   
        ) {
            log.info("SKIP");
            log.info("REQUEST HEADER: {}", request.getHeaderNames().toString());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

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

                log.error("UNATHORIZED");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        log.info("NEXT FILTERS");
        filterChain.doFilter(request, response);
    }
}
