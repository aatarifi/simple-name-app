package com.example.name_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Create Specific Exception Classes (Best Practice)
 * Create a dedicated exception class for each specific HTTP status.
 * This keeps your code clean, self-documenting, and easy to maintain.
 */

@ResponseStatus(HttpStatus.NOT_FOUND)  // Return 404
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }

}
