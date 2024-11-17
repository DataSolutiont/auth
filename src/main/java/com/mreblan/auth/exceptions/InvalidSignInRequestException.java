package com.mreblan.auth.exceptions;

public class InvalidSignInRequestException extends RuntimeException {
    public InvalidSignInRequestException(String msg) {
        super(msg);
    }
}
