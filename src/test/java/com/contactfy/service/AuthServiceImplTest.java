package com.contactfy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.contactfy.api.dto.AuthRequest;
import com.contactfy.api.dto.TokenDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.common.MessageKeyEnum;
import com.contactfy.config.security.JwtUtil;
import com.contactfy.exception.UnauthorizedLoginException;
import com.contactfy.exception.UserMissingAuthParameters;
import com.contactfy.service.impl.AuthServiceImpl;

public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidAuthRequest_whenAuthenticate_thenReturnsToken() {
        AuthRequest authRequest = new AuthRequest("user", "pass");
        TokenDTO tokenDTO = new TokenDTO("token", new Date());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");
        when(jwtUtil.generateToken("user")).thenReturn(
            new ApiResponse<>("1.0", MessageKeyEnum.TOKEN_GENERATED.getMsgCode(), tokenDTO, HttpStatus.OK)
        );

        ApiResponse<TokenDTO> response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("token", response.getContent().getToken());
        assertEquals("1.0", response.getApiVersion());
        assertEquals(MessageKeyEnum.TOKEN_GENERATED.getMsgCode(), response.getMessage());
    }

    @Test
    void givenMissingLogin_whenAuthenticate_thenThrowsUserMissingAuthParameters() {
        AuthRequest authRequest = new AuthRequest("", "pass");
        assertThrows(UserMissingAuthParameters.class, () -> authService.authenticate(authRequest));
    }

    @Test
    void givenMissingPassword_whenAuthenticate_thenThrowsUserMissingAuthParameters() {
        AuthRequest authRequest = new AuthRequest("user", "   ");
        assertThrows(UserMissingAuthParameters.class, () -> authService.authenticate(authRequest));
    }

    @Test
    void givenInvalidCredentials_whenAuthenticate_thenThrowsUnauthorizedLoginException() {
        AuthRequest authRequest = new AuthRequest("user", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("auth failed"));

        assertThrows(UnauthorizedLoginException.class, () -> authService.authenticate(authRequest));
    }
}