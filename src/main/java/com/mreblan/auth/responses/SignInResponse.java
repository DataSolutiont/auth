package com.mreblan.auth.responses;

import lombok.Data;

@Data
public class SignInResponse extends Response {
    private String token;

    public SignInResponse(boolean success, String desc, String token) {
        super(success, desc);
        this.token = token;
    }
}
