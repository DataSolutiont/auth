package com.mreblan.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mreblan.auth.services.impl.RedisService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("test")
public class TestController {

    private final RedisService redisService;
    
    @Deprecated
    @GetMapping("/name")
    public String getUsername() {
        return "TEST NAME";
    }

    @GetMapping("/revoke")
    public String revokeTokenTest(@RequestBody String token) {
        try {
            redisService.addTokenToCache(token);
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
            // return "ERROR";
        }

        return "Token revoked";
    }

    @GetMapping("/delete")
    public String deleteTokenTest(@RequestBody String token) {
        try {
            redisService.deleteTokenFromCache(token);
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
            // return "ERROR"
        }

        return "Token deleted";
    }


    @GetMapping("/find")
    public String findTokenTest(@RequestBody String token) {
        boolean isValid = false;
        try {
            isValid = redisService.isTokenInCache(token);
            return isValid ? "Token in cache" : "Token is not in cache";
        } catch (JwtException e) {
            log.error("JWT EXCEPTION");
        }

        return isValid ? "Token in cache" : "Token is not in cache";
    }
}
