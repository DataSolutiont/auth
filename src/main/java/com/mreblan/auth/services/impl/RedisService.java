package com.mreblan.auth.services.impl;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.exceptions.TokenNotRevokedException;
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

    @Override
    public void revokeToken(String token) throws JwtException {
        String key = null;
        try {
            key = formKey(token);
        } catch (JwtException e) {
            log.error(e.getMessage());
        }

        if (key != null) {
            repository.addToken(key, token);
        }
    }

    @Override
    public boolean isTokenRevoked(String token) throws JwtException {
        String key = null;
        try {
            key = formKey(token);
        } catch (JwtException e) {
            log.error(e.getMessage());
        }

        if (repository.findTokenByKey(key) != null && key != null) {
            return true;
        }

        return false;
    }

    @Override
    public void unrevokeToken(String token) throws JwtException {
        String key = null;
        try {
            key = formKey(token);
        } catch (JwtException e) {
            log.error(e.getMessage());
        }
        
        if (repository.findTokenByKey(key) != null && key != null) {
            repository.deleteTokenByKey(key);
        } else {
            log.error("Token is not revoked");
            throw new TokenNotRevokedException("Token is not revoked");
        }
    }

    private String formKey(String token) {
        return jwtService.getUsernameFromJwt(token) + jwtService.getIssuedAtFromJwt(token);
    }
}
