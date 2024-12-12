package com.example.PhoneShop.exception;


import com.example.PhoneShop.dto.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException e){
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(e.getMessage())
                .code(e.getCode())
                .build();
        return  ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField().toUpperCase() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(errorMessage)
                .code("global-e-01")
                .build();

        return ResponseEntity.status(400).body(response);
    }

}
