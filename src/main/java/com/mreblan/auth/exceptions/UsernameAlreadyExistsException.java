package com.mreblan.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Username already taken")
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }
}
