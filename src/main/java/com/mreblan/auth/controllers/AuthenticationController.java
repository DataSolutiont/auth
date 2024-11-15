package com.mreblan.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.http.HttpStatus;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.services.IAuthenticationService;
import com.mreblan.auth.services.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    
    private final IAuthenticationService authService;
    
    @PostMapping("/signup")
    public ResponseEntity signUpUser(@RequestBody SignUpRequest request) {

        log.info("SIGN UP REQUEST: {}", request.toString());


        try {
            authService.signUp(request);
        } catch(RuntimeException e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь существует");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Пользователь создан");
    }

    @PostMapping("/signin")
    public ResponseEntity signInUser(@RequestBody SignInRequest request) {
        log.info("SIGN IN REQUEST: {}", request.toString());

        User user = authService.signIn(request);

        if (user != null) {
            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Успешный вход");
        } else {
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Неверное имя пользователя или пароль");
        }
    }
}
