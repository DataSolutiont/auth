package com.mreblan.auth.requests;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Component
public class LogoutRequest {
    private String token;
}
