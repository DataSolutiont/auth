package com.mreblan.auth.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SignInResponse extends Response {
    private String token;

    public SignInResponse(boolean success, String description, String token) {
        super(success, description);
        this.token = token;
    }
}