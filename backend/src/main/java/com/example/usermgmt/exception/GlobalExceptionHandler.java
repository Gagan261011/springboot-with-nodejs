package com.example.usermgmt.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.NOT_FOUND, request, Collections.emptyList());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.CONFLICT, request, Collections.emptyList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> formatFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return buildErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed for request body",
                HttpStatus.UNPROCESSABLE_ENTITY,
                request,
                details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());
        return buildErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed for request parameters",
                HttpStatus.UNPROCESSABLE_ENTITY,
                request,
                details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.debug("Malformed request body: {}", ex.getMessage(), ex);
    return buildErrorResponse(
        "MALFORMED_REQUEST",
        "Request body is malformed or missing",
        HttpStatus.BAD_REQUEST,
        request,
        List.of("Malformed JSON in request body"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
    log.debug("Data integrity violation: {}", ex.getMessage(), ex);
    return buildErrorResponse(
        "DATA_INTEGRITY_VIOLATION",
        "Operation violates data integrity constraints",
        HttpStatus.CONFLICT,
        request,
        List.of("Operation violates data integrity constraints"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception processing request {} {}", request.getMethod(), request.getRequestURI(), ex);
    return buildErrorResponse(
        "INTERNAL_ERROR",
        "Unexpected error occurred",
        HttpStatus.INTERNAL_SERVER_ERROR,
        request,
        List.of("An unexpected error occurred"));
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            String code, String message, HttpStatus status, HttpServletRequest request, List<String> details) {
        ApiError error = ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(status).body(error);
    }

    private String formatFieldError(String field, String message) {
        return "%s: %s".formatted(field, message);
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage());
    }
}

