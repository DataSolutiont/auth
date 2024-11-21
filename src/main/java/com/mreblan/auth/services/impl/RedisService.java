package com.mreblan.auth.services.impl;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.repositories.IRedisRepository;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IRevokeService;
import com.mreblan.auth.services.IUserService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService implements IRevokeService {
    
    private final IRedisRepository repository;
    private final IJwtService      jwtService;
    // private final UserServiceImpl     userService;

    @Override
    public void revokeToken(String token) throws JwtException {
        String username = null;
        username = jwtService.getUsernameFromJwt(token);

        log.info("USERNAME: {}", username);

        if (
        repository.findTokenByUsername(username) != null ||
        isTokenRevoked(token)
        ) {
            log.info("OLD TOKEN DELETED");
            unrevokeToken(token);
        }

        log.info("TOKEN {} ADDED TO REVOKE CACHE", token);
        repository.addToken(username, token);
    }

    @Override
    public boolean isTokenRevoked(String token) throws JwtException {
        String result;

        String username = null;
        username = jwtService.getUsernameFromJwt(token);

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
    public void unrevokeToken(String token) throws JwtException {
        String username = null;
        username = jwtService.getUsernameFromJwt(token);

        log.info("USERNAME: {}", username);

        log.info("TOKEN {} DELETED FROM REVOKE CACHE", token);
        repository.deleteTokenByUsername(username);
    }
}
