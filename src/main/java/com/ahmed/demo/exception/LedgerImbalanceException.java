package com.ahmed.demo.exception;

public class LedgerImbalanceException extends RuntimeException{
    public LedgerImbalanceException(String message) {
        super(message);
    }
}
