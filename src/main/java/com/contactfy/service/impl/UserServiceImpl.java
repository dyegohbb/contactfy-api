package com.contactfy.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.contactfy.adapter.ApiResponseAdapter;
import com.contactfy.adapter.UserAdapter;
import com.contactfy.api.dto.UserCreateRequest;
import com.contactfy.api.dto.UserDTO;
import com.contactfy.api.dto.UserUpdateRequest;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.common.MessageKeyEnum;
import com.contactfy.entity.User;
import com.contactfy.exception.DuplicatedUserException;
import com.contactfy.exception.ForbiddenException;
import com.contactfy.exception.UserMissingCreateParameters;
import com.contactfy.exception.UserNotFoundException;
import com.contactfy.exception.UserNullException;
import com.contactfy.repository.jpa.UserRepository;
import com.contactfy.service.UserService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;
    private final ApiResponseAdapter apiResponseAdapter;
    private final UserAdapter userAdapter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsernameOrEmail(String login) {
        log.info("[USER][GET][{}] Getting logged user by username or email", login);
	
        User user = userRepository.findByUsernameOrEmail(login)
            .orElseThrow(() -> {
                log.warn("[USER][GET][{}] User not found with username or email", login);
                return new UserNotFoundException(login);
            });

        log.info("[USER][GET][{}] Successfully retrieved user with username or email", login);

        return user;
    }

    @Override
    public ApiResponse<UserDTO> createUser(UserCreateRequest userRequest) {

	validateRequiredFieldsToCreate(userRequest);
	
	String username = userRequest.username();
	String email = userRequest.email();
	
	log.info("[USER][CREATE] Starting User creation for user with details: username={}, email={}", username, email);
	
	if (userRepository.existsByUsernameOrEmail(username, email)) {
	    log.warn("[USER][CREATE] User creation for user with details: username={}, email={} - Duplicated username or email", username, email);
	    throw new DuplicatedUserException();
	}
	
	User user = userAdapter.toEntity(userRequest);
        userRepository.save(user);

        log.info("[USER][CREATE][{}] User with details: username={}, email={} successfully created", user.getUsername(), username, email);
        return apiResponseAdapter.toSuccess(userAdapter.toDTO(user), MessageKeyEnum.USER_CREATED, HttpStatus.CREATED);
    }

    private void validateRequiredFieldsToCreate(UserCreateRequest userRequest) {
	if (userRequest == null) {
	    throw new UserNullException();
	}
	
	List<String> requiredFieldsMissing = new ArrayList<>();

	String username = userRequest.username();
	if (StringUtils.isBlank(username)) {
	    requiredFieldsMissing.add("username");
	}

	if (StringUtils.isBlank(userRequest.email())) {
	    requiredFieldsMissing.add("email");
	}

	if (StringUtils.isBlank(userRequest.password())) {
	    requiredFieldsMissing.add("password");
	}
	
	if(!requiredFieldsMissing.isEmpty()) {
	    log.info("[USER][CREATE] User creation failed - Missing required parameters: {}", requiredFieldsMissing);
	    throw new UserMissingCreateParameters(requiredFieldsMissing.toString());
	}	
    }

    @Override
    public ApiResponse<UserDTO> updateUser(String loggedUsername, UserUpdateRequest userRequest, String identifier) {
        log.info("[USER][UPDATE][{}] Starting update process for user with identifier: {}", loggedUsername, identifier);

        User loggedUser = userRepository.findByUsernameOrEmail(loggedUsername).orElseThrow(() -> {
            log.error("[USER][UPDATE][{}] User with identifier {} not found", loggedUsername,
        	    identifier);
            throw new UserNotFoundException(identifier);
        });
        
        User user = userRepository.findByIdentifier(identifier).orElseThrow(() -> {
            log.error("[USER][UPDATE][{}] User with identifier {} not found", loggedUsername,
        	    identifier);
            throw new UserNotFoundException(identifier);
        });
        
        if (!loggedUser.getIdentifier().equals(identifier)) {
            log.warn("[USER][UPDATE][{}] User is not authorized to update user with identifier {}.", loggedUsername, identifier);
            throw new ForbiddenException("Update user with identifier: " + identifier);
        }
        
	if (userRepository.existsByUsernameOrEmailAndIdentifierNot(userRequest.username(), userRequest.email(),
		identifier)) {
	    log.warn(
		    "[USER][UPDATE] User update for user with details: username={}, email={} - Duplicated username or email",
		    user.getUsername(), user.getEmail());
	    throw new DuplicatedUserException();
	}
        
        updateUserFields(loggedUsername, user, userRequest);
        
        userRepository.save(user);
        log.info("[USER][UPDATE][{}] User update successful.", loggedUsername);
        
        return apiResponseAdapter.toSuccess(userAdapter.toDTO(user), MessageKeyEnum.USER_UPDATED, HttpStatus.OK);
    }
    
    private void updateUserFields(String loggedUsername, User user, UserUpdateRequest userRequest) {
	if (userRequest.username() != null && !userRequest.username().isBlank()) {
            user.setUsername(userRequest.username());
        }
        
        if (userRequest.email() != null && !userRequest.email().isBlank()) {
            user.setEmail(userRequest.email());
        }
        
        if (userRequest.password() != null && !userRequest.password().isBlank()) {
            if (!passwordEncoder.matches(userRequest.currentPassword(), user.getPassword())) {
                log.warn("[USER][UPDATE][{}] User attempted to change password with incorrect current password.", loggedUsername);
                throw new ForbiddenException("Update user with identifier: " + user.getIdentifier());
            }

            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }
        
        user.setUpdatedOn(LocalDateTime.now());

    }

    @Override
    public ApiResponse<Void> deleteUserByIdentifier(String loggedUsername, String identifier) {
        log.info("[USER][DELETE][{}] Starting delete process for user with identifier {}.", loggedUsername, identifier);

        User loggedUser = getUserByUsernameOrEmail(loggedUsername);

        if (!loggedUser.getIdentifier().equals(identifier)) {
            log.warn("[USER][DELETE][{}] User is not authorized to delete user with identifier {}.", loggedUsername, identifier);
            throw new ForbiddenException("Delete user with identifier: " + identifier);
        }

        loggedUser.logicDelete();
        userRepository.save(loggedUser);

        log.info("[USER][DELETE][{}] User with identifier {} successfully logically deleted.", loggedUsername, identifier);

        return apiResponseAdapter.toSuccess(null, MessageKeyEnum.USER_DELETED, HttpStatus.OK);
    }
    
}
