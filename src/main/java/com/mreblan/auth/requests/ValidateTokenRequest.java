package com.mreblan.auth.requests;

import lombok.Data;

@Data
public class ValidateTokenRequest {
    private String token;
}
