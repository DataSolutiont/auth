package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.Role;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.exceptions.InvalidSignUpRequestException;
import com.mreblan.auth.exceptions.IllegalRoleException;
import com.mreblan.auth.exceptions.IncorrectPasswordException;
import com.mreblan.auth.exceptions.InvalidSignInRequestException;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.requests.ValidateTokenRequest;
import com.mreblan.auth.services.IAuthenticationService;
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IRevokeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserServiceImpl userService;
    private final IJwtService     jwtService;
    private final IRevokeService  revokeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signUp(SignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role role;

        log.info("ROLE IN REQUEST: {}", request.getRole());

        if (
        request.getUsername().equals("") ||
        request.getPassword().equals("") ||
        request.getEmail().equals("") ||
        request.getRole().equals("")
        ) {
            throw new InvalidSignUpRequestException("Required args are null"); 
        }

        if (
        !request.getRole().toUpperCase().equals("CANDIDATE") &&
        !request.getRole().toUpperCase().equals("HR")        &&
        !request.getRole().toUpperCase().equals("ADMIN")
        ) {
            throw new IllegalRoleException("Role in request is not identified");
        }

        role = Role.valueOf(request.getRole());

        User userToRegister = User.builder()
                                .fio(request.getFio())
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(encodedPassword)
                                .companyName(
                                    role == Role.CANDIDATE ? null : request.getCompanyName()
                                )
                                .role(role)
                                .build();
        
        return userService.createUser(userToRegister);
    }

    @Override
    public String signIn(SignInRequest request) {
        User userToSignIn;

        if (request.getUsername().equals("") || request.getPassword().equals("")) {
            throw new InvalidSignInRequestException("Required args are null");
        }

        userToSignIn = (User) userService.loadUserByUsername(request.getUsername());
        log.info("USER TO SIGN IN: {}", userToSignIn.toString());


        if (passwordEncoder.matches(request.getPassword(), userToSignIn.getPassword())) {
            return jwtService.generateToken(userToSignIn);
        } else {

            log.error("PASSWORD ARE NOT CORRECT FOR USER WITH USERNAME: {}", userToSignIn.getUsername());

            throw new IncorrectPasswordException("Password is incorrect");
        }
    }

    public boolean validateToken(String token) {
        if (
            jwtService.isTokenValid(token) &&
            !revokeService.isTokenRevoked(token)
        ) {
            return true;
        }

        return false;
    }
}
