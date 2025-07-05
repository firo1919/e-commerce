package com.firomsa.ecommerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.firomsa.ecommerce.EcommerceApplication;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final EcommerceApplication ecommerceApplication;

    GlobalExceptionHandler(EcommerceApplication ecommerceApplication) {
        this.ecommerceApplication = ecommerceApplication;
    }

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException exception){
        Map<String, String> error = new HashMap<>();
        log.warn("resource with this id doesnt exist: {} ", exception.getMessage());
        error.put("message", "resource with this id doesnt exist " + exception.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, String>> handleStorageException(StorageException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage(), exception.getCause().getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(LimitedProductStockException.class)
    public ResponseEntity<Map<String, String>> handleLimitedProductStockException(LimitedProductStockException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException exception){
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message",exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        Map<String, String> error = new HashMap<>();
        log.warn(exception.getMessage());
        error.put("message", "something happened in the server");
        
        return ResponseEntity.internalServerError().body(error);
    }

}
