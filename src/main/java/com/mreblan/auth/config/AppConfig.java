package com.mreblan.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mreblan.auth.repositories.UserRepository;
import com.mreblan.auth.services.IUserService;
import com.mreblan.auth.services.impl.UserServiceImpl;

@Configuration
public class AppConfig {
    
    // @Bean
    // public BCryptPasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    //
    @Bean
    @Autowired
    public IUserService userService(UserRepository repository) {
        return new UserServiceImpl(repository);
    }
}
