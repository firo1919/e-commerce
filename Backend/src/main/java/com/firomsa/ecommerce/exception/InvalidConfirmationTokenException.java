package com.firomsa.ecommerce.exception;

public class InvalidConfirmationTokenException extends RuntimeException{
    public InvalidConfirmationTokenException(String message){
        super(message);
    }
}
