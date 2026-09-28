package com.example.invitedate.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvitationNotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(InvitationNotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, Object>> invalid(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Validation failed or request body is invalid", request);
    }
    @ExceptionHandler(InvalidInvitationStateException.class)
    ResponseEntity<Map<String, Object>> conflict(InvalidInvitationStateException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), request);
    }
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(Map.of("timestamp", OffsetDateTime.now().toString(),
                "status", status.value(), "error", status.getReasonPhrase(), "message", message, "path", req.getRequestURI()));
    }
}
