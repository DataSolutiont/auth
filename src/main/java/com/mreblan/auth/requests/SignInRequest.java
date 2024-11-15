package com.mreblan.auth.requests;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Component
public class SignInRequest {
    @Size(min = 5, max = 20, message = "Имя пользователя должно содержать от 5 до 20 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
