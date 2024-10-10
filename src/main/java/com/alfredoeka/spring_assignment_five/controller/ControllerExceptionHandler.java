package com.alfredoeka.spring_assignment_five.controller;

import com.alfredoeka.spring_assignment_five.controller.exception.InvalidPayloadException;
import com.alfredoeka.spring_assignment_five.controller.exception.UserAlreadyExistsException;
import com.alfredoeka.spring_assignment_five.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPayloadException(InvalidPayloadException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Add more exception handlers as needed
}