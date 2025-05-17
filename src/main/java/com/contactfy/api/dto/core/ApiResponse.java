package com.contactfy.api.dto.core;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResponse<T> {

    private String apiVersion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T content;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;
    private LocalDateTime timestamp;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hasNextPage;

    @JsonIgnore
    private HttpStatus status;

    public ApiResponse(String apiVersion, String message, T content, HttpStatus status) {
	this.apiVersion = apiVersion;
	this.message = message;
	this.content = content;
	this.status = status;
	this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String apiVersion, String message) {
	this.apiVersion = apiVersion;
	this.message = message;
	this.timestamp = LocalDateTime.now();
    }

}
