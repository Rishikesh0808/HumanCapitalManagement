package com.project.hcm.exception;

import com.project.hcm.dto.CustomResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomResponseDto> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        Object data = ex.getReason() != null ? ex.getReason() : "Request failed";
        return ResponseEntity.status(status)
                .body(new CustomResponseDto(status.value(), data));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseDto> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        Map.of("error", "Internal server error")
                ));
    }
}
