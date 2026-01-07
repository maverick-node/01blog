package com.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    // Constructor with custom message
    public ForbiddenException(String message) {
        super(message);
    }


}
