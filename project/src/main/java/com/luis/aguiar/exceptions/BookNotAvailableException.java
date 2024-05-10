package com.luis.aguiar.exceptions;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String s) {
        super(s);
    }
}
