// Centralized ControllerAdvice translating domain exceptions to RFC 7807 problem details
package com.naditech.naditech.infrastructure.adapter.inbound.exception;

import com.naditech.naditech.domain.exception.*;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ApiResponse;
import com.naditech.naditech.infrastructure.adapter.inbound.rest.dto.response.ErrorHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorHttpResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("User already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorHttpResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorHttpResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorHttpResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email/phone or password", request.getRequestURI(), null);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorHttpResponse> handleAccountLocked(AccountLockedException ex, HttpServletRequest request) {
        log.warn("Account locked: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.LOCKED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ErrorHttpResponse> handleAccountSuspended(AccountSuspendedException ex, HttpServletRequest request) {
        log.warn("Account suspended: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorHttpResponse> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        log.warn("Token exception: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorHttpResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation error on {}: {}", request.getRequestURI(), errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorHttpResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorHttpResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHttpResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please contact system support.", request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorHttpResponse> buildErrorResponse(HttpStatus status, String message, String path, Map<String, String> validationErrors) {
        ErrorHttpResponse response = ErrorHttpResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }
}
