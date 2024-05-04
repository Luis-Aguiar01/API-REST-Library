package com.luis.aguiar.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(UniqueDataViolationException.class)
    public ResponseEntity<ErrorModel> uniqueDataViolationException(UniqueDataViolationException ex, HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Unique data violation.", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorModel> entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Entity not found.", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    private ErrorModel configNewExceptionData(Exception ex, HttpServletRequest request, String error, HttpStatus status) {
        var exception = new ErrorModel();

        exception.setTimestamp(Instant.now());
        exception.setError(error);
        exception.setMessage(ex.getMessage());
        exception.setStatus(status.value());
        exception.setPath(request.getRequestURI());

        return exception;
    }
}
