package com.aaseya.MDM.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        long maxSizeInMB = ex.getMaxUploadSize() / 1024 / 1024; // Convert bytes to MB
        ErrorResponse errorResponse = new ErrorResponse("Maximum upload size of " + maxSizeInMB + " MB exceeded");
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // You can add more exception handlers here for other exceptions if needed
}