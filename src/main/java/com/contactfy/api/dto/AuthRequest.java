package com.contactfy.api.dto;

public record AuthRequest(
	String login, 
	String password) {
}
