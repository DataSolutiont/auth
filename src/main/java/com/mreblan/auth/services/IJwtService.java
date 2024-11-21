package com.mreblan.auth.services;

import com.mreblan.auth.entities.User;

public interface IJwtService {
    public String  generateToken(User user);
    public boolean isTokenValid(String token);
    public String  getUsernameFromJwt(String token);
    public String  getIssuedAtFromJwt(String jwt);
}
