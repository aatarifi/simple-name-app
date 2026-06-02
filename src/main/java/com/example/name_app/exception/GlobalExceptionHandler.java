package com.example.name_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice // Automatically intercepts all exceptions thrown by any Controller layer
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex){
        /*
        **** LinkedHashMap preserves insertion order, while HashMap does not. ****
        * Use LinkedHashMap when the order of keys matters,
        * such as when generating readable JSON payloads.
        ** Use HashMap when order is irrelevant, as it uses slightly less memory.
        */
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp" , LocalDateTime.now());
        body.put("status" , HttpStatus.BAD_REQUEST.value());
        body.put("error" , "Bad Request");
        body.put("message" , ex.getMessage());

        // Preferred: Clean, chainable, modern
        return ResponseEntity.badRequest().body(body);

        // Legacy: Verbosely passing arguments to a constructor
        // return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp" , LocalDateTime.now());
        body.put("status" , HttpStatus.NOT_FOUND.value());
        body.put("error" , "Not Found");
        body.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }




    /*  Or
    @ExceptionHandler(ResourceNotFoundException.class)

    // different way of return

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex){
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp" , LocalDateTime.now());
        body.put("status" , HttpStatus.NOT_FOUND.value());
        body.put("error" , "not Found");
        body.put("message" , ex.getMessage());

        return body;
    }
    */


}
