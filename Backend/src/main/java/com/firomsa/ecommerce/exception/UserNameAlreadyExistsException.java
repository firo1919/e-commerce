package com.firomsa.ecommerce.exception;

public class UserNameAlreadyExistsException extends RuntimeException{
    public UserNameAlreadyExistsException(String message){
        super(message);
    }
}
