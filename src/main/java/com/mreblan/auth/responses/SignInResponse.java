package com.mreblan.auth.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse extends Response {
    private String token;

    public SignInResponse(boolean success, String description, String token) {
        super(success, description);
        this.token = token;
    }
}
