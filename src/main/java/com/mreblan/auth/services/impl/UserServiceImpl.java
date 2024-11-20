package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.Role;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.exceptions.EmailAlreadyExistsException;
import com.mreblan.auth.exceptions.UsernameAlreadyExistsException;
import com.mreblan.auth.services.IUserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.mreblan.auth.repositories.UserRepository;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;

@Data
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository repository;

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User createUser(User user) throws RuntimeException {
        if (repository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("User with this username already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("User with this email already exists");
        }

        return saveUser(user);
    }

    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
