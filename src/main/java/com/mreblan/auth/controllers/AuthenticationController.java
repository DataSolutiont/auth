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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Register user in system",
        description = "Creates new user in database"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201",
            description = "Successful registration",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Пользователь создан\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Something went wrong during registration",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Пользователь с таким именем уже существует\"}"
                    )
                )
            )
        }
    )
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
                .status(HttpStatus.CREATED)
                .body(new Response(true, "Пользователь создан"));
    }


    @Operation(summary = "Sign in",
        description = "Finds user in database with username and password"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Successful registration",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Успешный вход\", \"token\": \"<JWT-token>\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Required args are null or invalid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Обязательные поля пусты\", \"token\": \"\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "404",
            description = "User with this username not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Пользователь с таким именем не найден\", \"token\": \"\"}"
                    )
                )
            )
        }
    )
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

    
    @Operation(summary = "Logout",
        description = "Revoke user's token"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Token revoked",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Токен отменён\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Token expired, not provided or already revoked",

            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"В запросе отсутствует токен\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<Response> logoutUser(@RequestBody LogoutRequest request) {
        String token = request.getToken();

        if (token == null) {
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "В запросе отсутствует токен"));
        }

        if (revokeService.isTokenRevoked(token)) {
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, "Токен уже отменён"));
        }

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
