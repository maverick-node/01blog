package com.Exceptions;

// 401 Unauthorized
public class BannedUserExceptions extends RuntimeException {
    public BannedUserExceptions(String message) {
        super(message);
    }
}
