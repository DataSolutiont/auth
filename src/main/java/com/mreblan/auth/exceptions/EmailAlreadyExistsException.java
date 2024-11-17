package com.mreblan.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Email already taken")
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }
}
