package ru.veselov.instazoo.exception;

import javax.naming.AuthenticationException;
import javax.security.auth.login.CredentialExpiredException;

public class CustomJwtException extends CredentialExpiredException {

    public CustomJwtException(String message) {
        super(message);
    }
}
