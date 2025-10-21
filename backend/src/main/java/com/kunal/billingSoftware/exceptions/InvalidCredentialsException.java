package com.kunal.billingSoftware.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
