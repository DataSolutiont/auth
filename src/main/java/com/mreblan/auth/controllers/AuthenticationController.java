package com.mreblan.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mreblan.auth.exceptions.EmailAlreadyExistsException;
import com.mreblan.auth.exceptions.IllegalRoleException;
import com.mreblan.auth.exceptions.IncorrectPasswordException;
import com.mreblan.auth.exceptions.InvalidSignInRequestException;
import com.mreblan.auth.exceptions.InvalidSignUpRequestException;
import com.mreblan.auth.exceptions.UsernameAlreadyExistsException;
import com.mreblan.auth.requests.LogoutRequest;
import com.mreblan.auth.requests.SignInRequest;
import com.mreblan.auth.requests.SignUpRequest;
import com.mreblan.auth.responses.Response;
import com.mreblan.auth.responses.SignInResponse;
import com.mreblan.auth.services.IAuthenticationService;
import com.mreblan.auth.services.IRevokeService;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(info = @Info(title = "Authentication API", version = "v1"))
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    
    private final IAuthenticationService authService;
    private final IRevokeService revokeService; 

    
    @PostMapping("/signup")
    public ResponseEntity<Response> signUpUser(@RequestBody SignUpRequest request) {

        log.info("SIGN UP REQUEST: {}", request.toString());

        try {
            authService.signUp(request);
        } catch (InvalidSignUpRequestException e) {
            log.error("Required args are null");

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Обязательные поля пусты"));
        } catch (UsernameAlreadyExistsException e) {
            // e.printStackTrace();

            log.error("Username already exists");

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Пользователь с таким именем уже существует"));
        } catch (EmailAlreadyExistsException e) {
            // e.printStackTrace();

            log.error("Email already exists");

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Пользователь с таким email уже существует"));
        } catch (IllegalRoleException e) {
            // e.printStackTrace();
            
            log.error("Illegal role provided");

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Роль указана неверно"));
        }

        log.info("New user with username --- {}  --- created!", request.getUsername());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new Response(true, "Пользователь создан"));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signInUser(@RequestBody SignInRequest request) {
        log.info("SIGN IN REQUEST: {}", request.toString());

        String jwt;

        try {
            jwt = authService.signIn(request);
        } catch (InvalidSignInRequestException e) {
            log.error("Required args are null");

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new SignInResponse(false, "Обязательные поля пусты", ""));
        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new SignInResponse(false, "Пользователь с таким именем не найден", ""));
        } catch (IncorrectPasswordException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new SignInResponse(false, "Неверный пароль", ""));
        }

        log.info("User with username --- {} --- signed in", request.getUsername());

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new SignInResponse(true, "Успешный вход", jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logoutUser(@RequestBody LogoutRequest request) {
        String token = request.getToken();

        try {
            revokeService.revokeToken(token);        
        } catch (ExpiredJwtException e) {
            log.error("JWT EXPIRED: {}", e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Токен уже истёк"));
        }
        log.info("TOKEN --- {} --- WAS REVOKED", token);

        return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Response(true, "Токен отменён"));
    }
}
