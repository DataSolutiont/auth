package com.mreblan.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.http.HttpStatus;

import com.mreblan.auth.entities.User;
import com.mreblan.auth.exceptions.EmailAlreadyExistsException;
import com.mreblan.auth.exceptions.IllegalRoleException;
import com.mreblan.auth.exceptions.InvalidSignInRequestException;
import com.mreblan.auth.exceptions.InvalidSignUpRequestException;
import com.mreblan.auth.exceptions.UsernameAlreadyExistsException;
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
    // private AuthenticationManager authenticationManager;
    //
    // @Autowired
    // public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    //     this.authenticationManager = authenticationManager; 
    // }

    
    @PostMapping("/signup")
    public ResponseEntity signUpUser(@RequestBody SignUpRequest request) {

        log.info("SIGN UP REQUEST: {}", request.toString());

        try {
            authService.signUp(request);
        } catch (InvalidSignUpRequestException e) {
            e.printStackTrace();

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Обязательные поля пусты");
        } catch (UsernameAlreadyExistsException e) {
            e.printStackTrace();

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Пользователь с таким именем уже существует");
        } catch (EmailAlreadyExistsException e) {
            e.printStackTrace();

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Пользователь с таким email уже существует");
        } catch (IllegalRoleException e) {
            e.printStackTrace();

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Роль указана неверно");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Пользователь создан");
    }

    @PostMapping("/signin")
    public ResponseEntity signInUser(@RequestBody SignInRequest request) {
        log.info("SIGN IN REQUEST: {}", request.toString());

        String jwt;

        try {
            jwt = authService.signIn(request);
        } catch (InvalidSignInRequestException e) {
            e.printStackTrace();

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Обязательные поля пусты");
        }

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(jwt);
    }
}
