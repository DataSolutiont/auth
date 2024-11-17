package com.mreblan.auth.services.impl;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
        log.debug("GENERATE TOKEN");
        Key key = makeSigningKey();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        log.debug("IS TOKEN VALID");
        SecretKey key = (SecretKey) makeSigningKey();
        
        try {
            Jws<Claims> claims = Jwts.parser()
                                    .verifyWith(key)
                                    .build()
                                    .parseSignedClaims(token);

            log.info("JWS CLAIMS: {}", claims.toString());

            return true;
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUsernameFromJwt(String token) {
        log.debug("GET USERNAME FROM JWT");

        SecretKey key = (SecretKey) makeSigningKey();

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();


    }

    private SecretKey makeSigningKey() {
        byte[] keyBytes = this.secret.getBytes();
        return new SecretKeySpec(keyBytes, io.jsonwebtoken.SignatureAlgorithm.HS256.getJcaName());
    }
}
