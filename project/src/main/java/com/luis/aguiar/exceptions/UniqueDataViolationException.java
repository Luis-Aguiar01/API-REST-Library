package com.luis.aguiar.exceptions;

public class UniqueDataViolationException extends RuntimeException{
    public UniqueDataViolationException() {
    }
    public UniqueDataViolationException(String message) {
        super(message);
    }
}
