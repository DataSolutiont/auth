package com.mreblan.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mreblan.auth.services.impl.RedisService;

import lombok.RequiredArgsConstructor;

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
        redisService.addTokenToCache(token);

        return "Token revoked";
    }

    @GetMapping("/delete")
    public String deleteTokenTest(@RequestBody String token) {
        redisService.deleteTokenFromCache(token);

        return "Token deleted";
    }


    @GetMapping("/find")
    public String findTokenTest(@RequestBody String token) {
        if (redisService.isTokenInCache(token)) {
            return "Token in cache";
        }

        return "Token is not in cache";
    }
}
