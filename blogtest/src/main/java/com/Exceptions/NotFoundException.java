package com.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This annotation makes Spring return HTTP 404 automatically
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  
    // Constructor with custom message
    public NotFoundException(String message) {
        super(message);
    }

   
  

 
    
}
