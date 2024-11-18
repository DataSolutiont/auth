package com.mreblan.auth.services;

public interface ICacheService {

    void    addTokenToCache(String token);
    boolean isTokenInCache(String token);
    void    deleteTokenFromCache(String token);
}
