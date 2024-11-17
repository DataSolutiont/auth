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
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.services.IAuthenticationService;
import com.mreblan.auth.services.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    
    private final IAuthenticationService authService;
    private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager; 
    }
    
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

        Authentication auth = null;
        try {
        auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = authService.signIn(request);
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(jwt);
    }

    @Deprecated
    @PostMapping("/valid")
    public ResponseEntity validateToken(@RequestBody String token) {
        boolean result = authService.checkToken(token);

        if (result) {
            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(result);
        } else {
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(result);
        }
    }
    
}
