package com.mreblan.auth.services;

public interface IRevokeService {

    void    revokeToken(String token);
    boolean isTokenRevoked(String token);
    void    unrevokeToken(String token);
}
