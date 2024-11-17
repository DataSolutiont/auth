package com.mreblan.auth.services.impl;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.services.IJwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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

        SecretKey key = makeSigningKey();

        String jwt = Jwts.builder()
                        .header()
                            .type("JWT")
                            .add("alg", "HS256")
                            .and()
                        .subject(user.getUsername())
                        // .claim("username", user.getUsername())
                        .claim("email", user.getEmail())
                        .signWith(key)
                        .expiration(new Date(System.currentTimeMillis() + expirationMs))
                        .compact();

        log.info("GENERATED JWT TOKEN: {}", jwt);

        return jwt;
    }

    @Override
    public boolean isTokenValid(String token) {
        log.debug("IS TOKEN VALID");
        SecretKey key = makeSigningKey();
        
        try {
            Jws<Claims> claims = Jwts.parser()
                                    .verifyWith(key)
                                    // .unsecured()
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
    public String getUsernameFromJwt(String token) throws JwtException {
        log.debug("GET USERNAME FROM JWT");

        SecretKey key = makeSigningKey();

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
