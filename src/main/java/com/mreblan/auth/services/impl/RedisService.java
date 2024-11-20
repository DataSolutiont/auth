package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.repositories.IRedisRepository;
import com.mreblan.auth.services.ICacheService;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IUserService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService implements ICacheService {
    
    private final IRedisRepository repository;
    private final IJwtService      jwtService;
    // private final UserServiceImpl     userService;

    @Override
    public void addTokenToCache(String token) {
        String username = null;
        try {
            username = jwtService.getUsernameFromJwt(token);
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
        }

        log.info("USERNAME: {}", username);

        if (
        repository.findTokenByUsername(username) != null ||
        isTokenInCache(token)
        ) {
            log.info("OLD TOKEN DELETED");
            deleteTokenFromCache(token);
        }

        log.info("TOKEN {} ADDED TO REVOKE CACHE", token);
        repository.addToken(username, token);
    }

    @Override
    public boolean isTokenInCache(String token) {
        String result;

        String username = null;
        try {
            username = jwtService.getUsernameFromJwt(token);
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
        }

        log.info("USERNAME: {}", username);

        result = repository.findTokenByUsername(username);

        log.info("RESULT FROM REDIS: {}", result);
        if (result != null && result.equals(token)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void deleteTokenFromCache(String token) {
        String username = null;
        try {
            username = jwtService.getUsernameFromJwt(token);
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
        }

        log.info("USERNAME: {}", username);

        log.info("TOKEN {} DELETED FROM REVOKE CACHE", token);
        repository.deleteTokenByUsername(username);
    }
}
