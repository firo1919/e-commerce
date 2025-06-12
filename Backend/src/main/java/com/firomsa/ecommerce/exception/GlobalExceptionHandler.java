package com.firomsa.ecommerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach( error -> errors.put(error.getField(), error.getDefaultMessage()) );
        
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailException(EmailAlreadyExistsException exception){
        Map<String, String> error = new HashMap<>();
        log.warn("Email address already exist: {}", exception.getMessage());
        error.put("message", "Email address already exists");
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserNameException(UserNameAlreadyExistsException exception){
        Map<String, String> error = new HashMap<>();
        log.warn("UserName already exist: {}", exception.getMessage());
        error.put("message", "UserName already exists");
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException exception){
        Map<String, String> error = new HashMap<>();
        log.warn("User with this id doesnt exist: {}", exception.getMessage());
        error.put("message", "User with this id doesnt exist");
        
        return ResponseEntity.badRequest().body(error);
    }

}
