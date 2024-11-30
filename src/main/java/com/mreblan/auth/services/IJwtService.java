package com.mreblan.auth.services;

import com.mreblan.auth.entities.Role;
import com.mreblan.auth.entities.User;

public interface IJwtService {
    public String  generateToken(User user);
    public boolean isTokenValid(String jwt);
    public String  getUsernameFromJwt(String jwt);
    public String  getIssuedAtFromJwt(String jwt);
    public Role    getRoleFromJwt(String jwt);
}
