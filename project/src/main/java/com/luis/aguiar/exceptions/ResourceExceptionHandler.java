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
        ErrorModel exception = new ErrorModel();
        exception.setTimestamp(Instant.now());
        exception.setError("Unique date violation.");
        exception.setMessage(ex.getMessage());
        exception.setStatus(HttpStatus.CONFLICT.value());
        exception.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception);
    }
}
