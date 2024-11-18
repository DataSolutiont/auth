package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.repositories.IRedisRepository;
import com.mreblan.auth.services.ICacheService;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService implements ICacheService {
    
    private final IRedisRepository repository;
    private final IJwtService      jwtService;
    private final UserServiceImpl     userService;

    @Override
    public void addTokenToCache(String token) {
        String username = jwtService.getUsernameFromJwt(token);
        Long id;

        try {
            User user = (User) userService.loadUserByUsername(username);
            id   = user.getId();
        } catch (UsernameNotFoundException e) {
            log.error("USER WITH NAME {} NOT FOUND", username);
            e.printStackTrace();
            return;
        }

        if (
        repository.findTokenByUsername(username, id) != null ||
        isTokenInCache(token)
        ) {
            log.info("OLD TOKEN DELETED");
            deleteTokenFromCache(token);
        }

        log.info("TOKEN {} ADDED TO REVOKE CACHE", token);
        repository.addToken(username, id, token);
    }

    @Override
    public boolean isTokenInCache(String token) {
        String result;
        Long id;

        String username = jwtService.getUsernameFromJwt(token);

        try {
            id = getIdByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.error("USER WITH NAME {} NOT FOUND", username);
            e.printStackTrace();
            return false;
        }

        result = repository.findTokenByUsername(username, id);

        log.info("RESULT FROM REDIS: {}", result);
        if (result != null && result.equals(token)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void deleteTokenFromCache(String token) {
        Long id;
        String username = jwtService.getUsernameFromJwt(token);

        try {
            id = getIdByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.error("USER WITH NAME {} NOT FOUND", username);
            e.printStackTrace();
            return;
        }
        
        log.info("TOKEN {} DELETED FROM REVOKE CACHE", token);
        repository.deleteTokenByUsername(username, id);
    }

    private Long getIdByUsername(String username) throws UsernameNotFoundException {
        User user = (User) userService.loadUserByUsername(username);

        return user.getId();
    }
}
