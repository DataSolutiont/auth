package com.mreblan.auth.services;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;

public interface IAuthenticationService {
    public User signUp(SignUpRequest request);
    public User signIn(SignInRequest request);
}
