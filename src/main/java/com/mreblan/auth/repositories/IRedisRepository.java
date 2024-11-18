package com.mreblan.auth.repositories;

public interface IRedisRepository {
    void                addToken(String username, Long id, String token);
    void                deleteTokenByUsername(String username, Long id);
    String              findTokenByUsername(String username, Long id);
}
