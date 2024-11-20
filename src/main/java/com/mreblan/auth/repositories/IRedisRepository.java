package com.mreblan.auth.repositories;

public interface IRedisRepository {
    void                addToken(String username, String token);
    void                deleteTokenByUsername(String username);
    String              findTokenByUsername(String username);
}
