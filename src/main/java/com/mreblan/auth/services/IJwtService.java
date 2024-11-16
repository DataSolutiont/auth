package com.mreblan.auth.services;

import com.mreblan.auth.entities.User;

public interface IJwtService {

    public String generateToken(User user);
    public boolean isTokenValid(String token);
}
