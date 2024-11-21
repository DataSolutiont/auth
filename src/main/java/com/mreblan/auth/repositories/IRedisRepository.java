package com.mreblan.auth.repositories;

public interface IRedisRepository {
    void                addToken(String key, String token);
    void                deleteTokenByKey(String key);
    String              findTokenByKey(String key);
}
