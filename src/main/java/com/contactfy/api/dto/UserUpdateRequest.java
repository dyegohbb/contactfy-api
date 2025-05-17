package com.contactfy.api.dto;

public record UserUpdateRequest(
	String username, 
	String email, 
	String password,
	String currentPassword) {
}
