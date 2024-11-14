package com.mreblan.auth.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.Role;
import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.IUserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.mreblan.auth.repositories.UserRepository;
import com.mreblan.auth.requests.SignUpRequest;

@Data
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(SignUpRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

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
        
        return saveUser(userToRegister);
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User createUser(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        return saveUser(user);
    }

    public UserDetails loadByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
