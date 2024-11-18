package com.mreblan.auth.repositories.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.mreblan.auth.repositories.IRedisRepository;

import jakarta.annotation.PostConstruct;

@Repository
@RedisHash
public class RedisRepositoryImpl implements IRedisRepository {

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void addToken(String username, Long id, String token) {
        hashOperations.put(username, id, token);

    }

    @Override
    public void deleteTokenByUsername(String username, Long id) {
        hashOperations.delete(username, id);
    }

    @Override
    public String findTokenByUsername(String username, Long id) {
        return (String) hashOperations.get(username, id);
    }
}
