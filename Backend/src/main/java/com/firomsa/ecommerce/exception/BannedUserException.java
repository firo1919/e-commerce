package com.firomsa.ecommerce.exception;

public class BannedUserException extends RuntimeException{
    public BannedUserException(String message){
        super("You have been banned for security reasons" + message);
    }
}
