package com.mreblan.auth.exceptions;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String msg) {
        super(msg);
    }
    
}
