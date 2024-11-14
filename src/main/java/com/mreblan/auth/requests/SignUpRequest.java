package com.mreblan.auth.requests;

import org.springframework.stereotype.Component;

import com.mreblan.auth.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Component
public class SignUpRequest {

    @NotBlank(message = "ФИО не может быть пустым")
    private String fio;

    @Size(min = 5, max = 20, message = "Имя пользователя должно содержать от 5 до 20 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Size(min = 10, max = 255, message = "Адрес электронной почты должен содержать от 10 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Адрес электронной почты должен быть в формате user@example.com")
    private String email;

    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    private String companyName;

    @NotBlank(message = "Необходимо указать роль пользователя")
    private Role role;
}
