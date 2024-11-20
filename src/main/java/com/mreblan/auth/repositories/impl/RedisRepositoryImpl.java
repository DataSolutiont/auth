package com.mreblan.auth.repositories.impl;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.mreblan.auth.repositories.IRedisRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RedisRepositoryImpl implements IRedisRepository {

    private StringRedisTemplate stringTemplate;
    private ValueOperations valueOperations;

    @Autowired
    public RedisRepositoryImpl(StringRedisTemplate stringTemplate) {
        this.stringTemplate = stringTemplate;
    }

    @PostConstruct
    public void init() {
        valueOperations = stringTemplate.opsForValue();
    }

    @Override
    public void addToken(String username, String token) {
        log.info("USERNAME AND TOKEN: {} \n{}", username, token);
        valueOperations.set(username, token, 10, TimeUnit.MINUTES);
    }

    @Override
    public void deleteTokenByUsername(String username) {
        stringTemplate.delete(username);
    }

    @Override
    public String findTokenByUsername(String username) {
        String result = (String) valueOperations.get(username);
        log.info("RESULT: {}", result);
        return (String) valueOperations.get(username);
    }
}
