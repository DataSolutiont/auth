package com.mreblan.auth.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.IJwtService;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtServiceImpl implements IJwtService {
    @Value("${auth.jwt.secret}")
    private  String secret;
    @Value("${auth.jwt.expirationMs}")
    private  long   expirationMs;

    // @Autowired
    // private Jwts jwts;
    
    @Override
    public String generateToken(User user) {
        String jwt = Jwts.builder()
                        .claim("username", user.getUsername())
                        .claim("email", user.getEmail())
                        .expiration(new Date(System.currentTimeMillis() + expirationMs))
                        .compact();

        log.info("GENERATED JWT TOKEN: {}", jwt);

        return jwt;
    }

    @Override
    public boolean checkToken(String token) {
        return false;
    }
}
