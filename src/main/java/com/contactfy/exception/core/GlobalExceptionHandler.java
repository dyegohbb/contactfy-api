package com.contactfy.exception.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.contactfy.adapter.ApiResponseAdapter;
import com.contactfy.api.dto.core.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ApiResponseAdapter apiResponseAdapter;
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException customException) {
	ApiResponse<Void> error = apiResponseAdapter.toError(customException.getMessageKeyEnum(), customException.getArgs());
        return new ResponseEntity<>(error, customException.getStatus());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception exception) {
	log.error("Unhandled exception occurred: {}", exception.getMessage(), exception);
	
	ApiResponse<Void> error = apiResponseAdapter.toError(ExceptionMessageKeyEnum.GENERIC_ERROR, null);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
