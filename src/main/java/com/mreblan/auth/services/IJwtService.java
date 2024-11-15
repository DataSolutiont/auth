package com.mreblan.auth.services;

import com.mreblan.auth.entities.User;

public interface IJwtService {

    public String generateToken(User user);
    public boolean checkToken(String token);
}
