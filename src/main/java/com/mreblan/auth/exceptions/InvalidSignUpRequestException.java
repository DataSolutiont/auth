package com.mreblan.auth.exceptions;

public class InvalidSignUpRequestException extends RuntimeException {
    public InvalidSignUpRequestException(String msg) {
        super(msg);
    }
}
