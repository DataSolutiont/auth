package com.mreblan.auth.repositories;

import org.springframework.data.redis.core.RedisHash;

@RedisHash
public interface IRedisRepository {
    void                addToken(String key, String token);
    void                deleteTokenByKey(String key);
    String              findTokenByKey(String key);
}
