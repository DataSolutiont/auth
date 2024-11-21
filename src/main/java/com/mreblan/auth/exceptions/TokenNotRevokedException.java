package com.mreblan.auth.exceptions;

public class TokenNotRevokedException extends RuntimeException {
    public TokenNotRevokedException(String msg) {
        super(msg);
    }
}
