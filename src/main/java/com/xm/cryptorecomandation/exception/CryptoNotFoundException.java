package com.xm.cryptorecomandation.exception;

public class CryptoNotFoundException extends RuntimeException{
    public CryptoNotFoundException(String message) {
        super(message);
    }
}
