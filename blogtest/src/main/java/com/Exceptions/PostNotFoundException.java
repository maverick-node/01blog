package com.Exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) { 
        super(message); 
    }
}
