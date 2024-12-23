package com.mreblan.auth.repositories.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final long TTL = 10;

    @Autowired
    public RedisRepositoryImpl(StringRedisTemplate stringTemplate) {
        this.stringTemplate = stringTemplate;
    }

    @PostConstruct
    public void init() {
        valueOperations = stringTemplate.opsForValue();
    }

    @Override
    public void addToken(String key, String token) {
        log.info("USERNAME AND TOKEN: {} \n{}", key, token);
        valueOperations.set(key, token, TTL, TimeUnit.MINUTES);
    }

    @Override
    public void deleteTokenByKey(String key) {
        stringTemplate.delete(key);
    }

    @Override
    public String findTokenByKey(String key) {
        String result = (String) valueOperations.get(key);
        log.info("RESULT: {}", result);
        return result;
    }
}
