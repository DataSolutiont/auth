package com.mreblan.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

import lombok.Data;

@Data
@Component
public class JWTCore {
    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.expirationMs}")
    private long expiration;
}
