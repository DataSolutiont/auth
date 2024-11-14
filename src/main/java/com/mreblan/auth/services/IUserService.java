package com.mreblan.auth.services;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.requests.SignUpRequest;

public interface IUserService {

    public User registerUser(SignUpRequest request);
    public User saveUser(User user);
    public User createUser(User user);
}
