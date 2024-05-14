package com.luis.aguiar.exceptions;

public class LoanNotAvailableException extends RuntimeException {
    public LoanNotAvailableException(String message) {
        super(message);
    }
}
