package com.mreblan.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mreblan.auth.entities.Role;
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
import com.mreblan.auth.services.IJwtService;
import com.mreblan.auth.services.IRevokeService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private final IRevokeService         revokeService; 
    private final IJwtService            jwtService;

    
    @PostMapping("/signup")
    @Operation(summary = "Регистрация",
        description = "Регистрирует пользователя в системе"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201",
            description = "Успешная регистрация",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Пользователь создан\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "При регистрации что-то пошло не так",
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


    @Operation(summary = "Вход в аккаунт",
        description = "Находит пользователя в БД и сверяет данные. Возвращает JWT-токен"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Успешный вход",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Успешный вход\", \"token\": \"<JWT-token>\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Обязательные поля пусты",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Обязательные поля пусты\", \"token\": \"\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "404",
            description = "Не найден пользователь с таким именем",
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

    
    @Operation(summary = "Выход из аккаунта",
        description = "Отменяет токен пользователя"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Токен отменён",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Токен отменён\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Токен истёк, не предоставлен или уже отменён",

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


    @Operation(summary = "Проверка роли пользователя для загрузки резюме",
        description = "Проверяет наличие токена и роль пользователя"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Доступ разрешён",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Разрешено\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "401",
            description = "С токеном что-то не так или неподходящая роль",

            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Доступ запрещён\" }"
                    )
                )
            )
        }
    )
    @GetMapping("/uploadCheck")
    public ResponseEntity<Response> uploadCheck(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Role roleFromToken = null;

        if (
            !jwtService.isTokenValid(token) ||
            revokeService.isTokenRevoked(token)
        ) {
            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Токен не валиден или уже отменён"));
        }

        try {
            roleFromToken = jwtService.getRoleFromJwt(token);
        } catch (JwtException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Не удалось получить роль из токена"));
        }

        if (
            roleFromToken == Role.CANDIDATE ||
            roleFromToken == Role.HR
        ) {
            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new Response(true, "Разрешено"));
        }

        return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new Response(false, "Доступ запрещён"));
    } 


    @Operation(summary = "Проверка роли пользователя поиска резюме",
        description = "Проверяет наличие токена и роль пользователя"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Доступ разрешён",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Разрешено\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "401",
            description = "С токеном что-то не так или неподходящая роль",

            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Доступ запрещён\" }"
                    )
                )
            )
        }
    )
    @GetMapping("/findCheck")
    public ResponseEntity<Response> findCheck(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Role roleFromToken = null;

        if (
            !jwtService.isTokenValid(token) ||
            revokeService.isTokenRevoked(token)
        ) {
            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Токен не валиден или уже отменён"));
        }

        try {
            roleFromToken = jwtService.getRoleFromJwt(token);
        } catch (JwtException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(false, "Не удалось получить роль из токена"));
        }

        if (roleFromToken == Role.HR) {
            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new Response(true, "Разрешено"));
        }

        return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new Response(false, "Доступ запрещён"));
    } 
}
