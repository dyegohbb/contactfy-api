package com.contactfy.api.dto;

public record UserCreateRequest(
	String username, 
	String email, 
	String password) {
}