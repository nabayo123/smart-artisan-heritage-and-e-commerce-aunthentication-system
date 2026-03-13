package com.korarwandasystem.korarwanda.exception;

import com.fasterxml.jackson.databind.JsonParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================================
    // 400 Bad Request - Validation Errors
    // ================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", "error");
        errors.put("message", "Validation failed");
        errors.put("details", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(errors);
    }

    // ================================
    // 400 Bad Request - JSON Parsing Errors
    // ================================
    @ExceptionHandler({JsonParseException.class, org.springframework.http.converter.HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(Exception ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", "error");
        errors.put("message", "Invalid JSON format. Please check your request body syntax.");
        errors.put("details", "Make sure JSON is properly formatted with correct field names and no syntax errors.");
        errors.put("suggestions", "1. Check for missing commas 2. Verify all quotes are closed 3. Ensure field names match entity properties");
        return ResponseEntity.badRequest().body(errors);
    }

    // ================================
    // 404 Not Found - Resource Missing
    // ================================
    @ExceptionHandler({NoHandlerFoundException.class, jakarta.persistence.EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "error");
        response.put("message", "Resource not found");
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ================================
    // 409 Conflict - Duplicate Entries
    // ================================
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "error");
        response.put("message", "Data integrity constraint violated");
        response.put("path", "");
        
        String message = ex.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            if (lowerMessage.contains("users.email") || lowerMessage.contains("duplicate entry") && lowerMessage.contains("email")) {
                response.put("message", "Email address already exists. Please use a different email address.");
                response.put("field", "email");
            } else if (lowerMessage.contains("certificates.certificate_number") || lowerMessage.contains("certificate_number")) {
                response.put("message", "Certificate number already exists. Please use a unique certificate number.");
                response.put("field", "certificate_number");
            } else if (lowerMessage.contains("phone") || lowerMessage.contains("phone_number")) {
                response.put("message", "Phone number already exists. Please use a different phone number.");
                response.put("field", "phone_number");
            } else if (lowerMessage.contains("artisans.email")) {
                response.put("message", "Artisan email already exists. Please use a different email address.");
                response.put("field", "email");
            } else {
                response.put("message", "Duplicate entry detected. This record already exists in the system.");
                response.put("field", "unknown");
            }
        } else {
            response.put("message", "Data integrity constraint violated: " + message);
            response.put("field", "database");
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ================================
    // 400 Bad Request - Illegal Arguments
    // ================================
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleIllegalArguments(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "error");
        response.put("message", "Invalid request: " + ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // ================================
    // 500 Internal Server Error - Catch All
    // ================================
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        response.put("details", ex.getMessage());
        
        // Log the full exception for debugging
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
