package com.mobile.pontoGestao.Erros;

public class AuthorizationTokenInvalidException extends RuntimeException {
    public AuthorizationTokenInvalidException(String message) {
        super(message);
    }
}
