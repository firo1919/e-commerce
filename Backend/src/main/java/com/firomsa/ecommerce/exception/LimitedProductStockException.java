package com.firomsa.ecommerce.exception;

public class LimitedProductStockException extends RuntimeException{
    public LimitedProductStockException(String message){
        super(message);
    }
}
