package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.Role;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.services.IAuthenticationService;
import com.mreblan.auth.services.IJwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserServiceImpl userService;
    private final IJwtService     jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signUp(SignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User userToRegister = User.builder()
                                .fio(request.getFio())
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(encodedPassword)
                                .companyName(
                                    request.getRole() == Role.CANDIDATE ? null : request.getCompanyName()
                                )
                                .role(request.getRole())
                                .build();
        
        return userService.createUser(userToRegister);
    }

    @Override
    public String signIn(SignInRequest request) {
        User userToSignIn;

        try {
            userToSignIn = (User) userService.loadUserByUsername(request.getUsername());
            log.info("USER TO SIGN IN: {}", userToSignIn.toString());


            if (passwordEncoder.matches(request.getPassword(), userToSignIn.getPassword())) {
                return jwtService.generateToken(userToSignIn);
            } else {

                log.error("PASSWORD ARE NOT CORRECT FOR USER WITH USERNAME: {}", userToSignIn.getUsername());

                return null;
            }

        } catch (UsernameNotFoundException e) {
            e.printStackTrace();

            log.error("USER WITH USERNAME - {} - NOT FOUND", request.getUsername());

            return null;
        } 
    }
}
