package com.contactfy.service;

import com.contactfy.api.dto.UserCreateRequest;
import com.contactfy.api.dto.UserDTO;
import com.contactfy.api.dto.UserUpdateRequest;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.entity.User;

public interface UserService {
    
    User getUserByUsernameOrEmail(String login);

    ApiResponse<UserDTO> createUser(UserCreateRequest userRequest);

    ApiResponse<UserDTO> updateUser(String name, UserUpdateRequest userRequest, String identifier);

    ApiResponse<Void> deleteUserByIdentifier(String name, String identifier);

}
