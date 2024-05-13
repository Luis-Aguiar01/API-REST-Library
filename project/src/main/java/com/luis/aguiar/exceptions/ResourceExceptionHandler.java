package com.luis.aguiar.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(UniqueDataViolationException.class)
    public ResponseEntity<ErrorModel> uniqueDataViolationException(UniqueDataViolationException ex,
                                                                   HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Unique data violation.", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorModel> entityNotFoundException(EntityNotFoundException ex,
                                                              HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Entity not found.", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ErrorModel> bookNotAvailableException(BookNotAvailableException ex,
                                                                HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Resource not available.", HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
    }

    @ExceptionHandler(LoanNotAvailableException.class)
    public ResponseEntity<ErrorModel> bookNotAvailableException(LoanNotAvailableException ex,
                                                                HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Resource not available.", HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorModel> methodArgumentNotValidException(MethodArgumentTypeMismatchException ex,
                                                                      HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Invalid UUID format.", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorModel> expiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        var exception = configNewExceptionData(ex, request, "Unauthorized.", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    private ErrorModel configNewExceptionData(Exception ex, HttpServletRequest request,
                                              String error,
                                              HttpStatus status) {
        var exception = new ErrorModel();

        exception.setTimestamp(Instant.now());
        exception.setError(error);
        exception.setMessage(ex.getMessage());
        exception.setStatus(status.value());
        exception.setPath(request.getRequestURI());

        return exception;
    }
}
