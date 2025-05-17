package com.contactfy.service;

import com.contactfy.adapter.ApiResponseAdapter;
import com.contactfy.adapter.UserAdapter;
import com.contactfy.api.dto.UserCreateRequest;
import com.contactfy.api.dto.UserDTO;
import com.contactfy.api.dto.UserUpdateRequest;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.common.MessageKeyEnum;
import com.contactfy.entity.User;
import com.contactfy.exception.*;
import com.contactfy.repository.jpa.UserRepository;
import com.contactfy.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApiResponseAdapter apiResponseAdapter;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidRequest_whenCreateUser_thenSuccess() {
        UserCreateRequest request = new UserCreateRequest("user", "email@test.com", "pass");
        User user = new User();
        UserDTO dto = new UserDTO();
        ApiResponse<UserDTO> expected = new ApiResponse<>("1.0", MessageKeyEnum.USER_CREATED.getMsgCode(), dto, HttpStatus.CREATED);

        when(userRepository.existsByUsernameOrEmail("user", "email@test.com")).thenReturn(false);
        when(userAdapter.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userAdapter.toDTO(user)).thenReturn(dto);
        when(apiResponseAdapter.toSuccess(dto, MessageKeyEnum.USER_CREATED, HttpStatus.CREATED)).thenReturn(expected);

        ApiResponse<UserDTO> response = userService.createUser(request);

        assertEquals(dto, response.getContent());
    }

    @Test
    void givenDuplicateUsernameOrEmail_whenCreateUser_thenThrowException() {
        UserCreateRequest request = new UserCreateRequest("user", "email@test.com", "pass");

        when(userRepository.existsByUsernameOrEmail("user", "email@test.com")).thenReturn(true);

        assertThrows(DuplicatedUserException.class, () -> userService.createUser(request));
    }

    @Test
    void givenNullRequest_whenCreateUser_thenThrowException() {
        assertThrows(UserNullException.class, () -> userService.createUser(null));
    }

    @Test
    void givenMissingFields_whenCreateUser_thenThrowException() {
        UserCreateRequest request = new UserCreateRequest(" ", null, "");

        assertThrows(UserMissingCreateParameters.class, () -> userService.createUser(request));
    }

    @Test
    void givenValidUpdateRequest_whenUpdateUser_thenSuccess() {
        String identifier = "id";
        String loggedUsername = "user";
        User loggedUser = User.builder().identifier(identifier).password("hashed").build();
        User user = User.builder().identifier(identifier).password("hashed").build();
        UserUpdateRequest request = new UserUpdateRequest("newUser", "new@test.com", "newPass", "hashed");
        UserDTO dto = new UserDTO();

        when(userRepository.findByUsernameOrEmail(loggedUsername)).thenReturn(Optional.of(loggedUser));
        when(userRepository.findByIdentifier(identifier)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameOrEmailAndIdentifierNot("newUser", "new@test.com", identifier)).thenReturn(false);
        when(passwordEncoder.matches("hashed", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(user);
        when(userAdapter.toDTO(user)).thenReturn(dto);
        when(apiResponseAdapter.toSuccess(dto, MessageKeyEnum.USER_UPDATED, HttpStatus.OK)).thenReturn(
            new ApiResponse<>("1.0", MessageKeyEnum.USER_UPDATED.getMsgCode(), dto, HttpStatus.OK));

        ApiResponse<UserDTO> response = userService.updateUser(loggedUsername, request, identifier);

        assertEquals(dto, response.getContent());
    }

    @Test
    void givenUnauthorizedUser_whenUpdateUser_thenThrowForbidden() {
        UserUpdateRequest request = new UserUpdateRequest("u", "e", "p", "cp");

        User logged = User.builder().identifier("logged").build();
        User target = User.builder().identifier("target").build();

        when(userRepository.findByUsernameOrEmail("logged")).thenReturn(Optional.of(logged));
        when(userRepository.findByIdentifier("target")).thenReturn(Optional.of(target));

        assertThrows(ForbiddenException.class, () -> userService.updateUser("logged", request, "target"));
    }

    @Test
    void givenUserNotFound_whenUpdateUser_thenThrowException() {
        UserUpdateRequest request = new UserUpdateRequest("u", "e", "p", "cp");

        when(userRepository.findByUsernameOrEmail("user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("user", request, "id"));
    }

    @Test
    void givenWrongPassword_whenUpdateUser_thenThrowForbidden() {
        String identifier = "id";
        String loggedUsername = "user";
        User loggedUser = User.builder().identifier(identifier).password("hashed").build();
        User user = User.builder().identifier(identifier).password("hashed").build();
        UserUpdateRequest request = new UserUpdateRequest("newUser", "new@test.com", "newPass", "wrong");

        when(userRepository.findByUsernameOrEmail(loggedUsername)).thenReturn(Optional.of(loggedUser));
        when(userRepository.findByIdentifier(identifier)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameOrEmailAndIdentifierNot("newUser", "new@test.com", identifier)).thenReturn(false);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> userService.updateUser(loggedUsername, request, identifier));
    }

    @Test
    void givenValidDelete_whenDeleteUser_thenSuccess() {
        String identifier = "id";
        User user = User.builder().identifier(identifier).build();

        when(userRepository.findByUsernameOrEmail("user")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(apiResponseAdapter.toSuccess(null, MessageKeyEnum.USER_DELETED, HttpStatus.OK)).thenReturn(
            new ApiResponse<>("1.0", MessageKeyEnum.USER_DELETED.getMsgCode(), null, HttpStatus.OK));

        ApiResponse<Void> response = userService.deleteUserByIdentifier("user", identifier);

        assertEquals("1.0", response.getApiVersion());
        assertEquals(MessageKeyEnum.USER_DELETED.getMsgCode(), response.getMessage());
    }

    @Test
    void givenUnauthorizedDelete_whenDeleteUser_thenThrowForbidden() {
        User user = User.builder().identifier("123").build();

        when(userRepository.findByUsernameOrEmail("user")).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> userService.deleteUserByIdentifier("user", "other-id"));
    }
}

