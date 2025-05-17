package com.contactfy.adapter;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.contactfy.api.dto.UserCreateRequest;
import com.contactfy.api.dto.UserDTO;
import com.contactfy.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter {
    
    private final PasswordEncoder passwordEncoder;
    
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .identifier(user.getIdentifier())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User toEntity(UserCreateRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        
	return User.builder()
		.identifier(UUID.randomUUID().toString())
		.username(userRequest.username())
		.email(userRequest.email())
		.password(encrypt(userRequest.password()))
		.build();
    }
    
    private String encrypt(String password) {
	return passwordEncoder.encode(password);
    }

}
