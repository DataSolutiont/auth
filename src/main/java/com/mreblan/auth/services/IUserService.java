package com.mreblan.auth.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mreblan.auth.entities.User;

public interface IUserService extends UserDetailsService {

    public User saveUser(User user);
    public User createUser(User user);
}
