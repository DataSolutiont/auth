package com.mreblan.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.services.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    
    private final IUserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<String> signUpUser(@RequestBody SignUpRequest request) {

        log.info("REQUEST: {}", request.toString());

        userService.registerUser(request);
        return ResponseEntity.ok("Пользователь зарегистрирован!");
    }
}
