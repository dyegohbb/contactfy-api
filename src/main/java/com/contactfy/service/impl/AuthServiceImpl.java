package com.contactfy.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.contactfy.api.dto.AuthRequest;
import com.contactfy.api.dto.TokenDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.config.security.JwtUtil;
import com.contactfy.exception.UnauthorizedLoginException;
import com.contactfy.exception.UserMissingAuthParameters;
import com.contactfy.service.AuthService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public ApiResponse<TokenDTO> authenticate(AuthRequest authRequest) {
	String usernameOrEmail = authRequest.login();
	validateAuthRequest(authRequest);
	log.info("[AUTH][{}] Authentication started", usernameOrEmail);

	try {
	    Authentication authentication = authenticationManager
		    .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, authRequest.password()));
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

	    ApiResponse<TokenDTO> token = jwtUtil.generateToken(userDetails.getUsername());

	    log.info("[AUTH][{}] User authenticated and token successfully generated", usernameOrEmail);
	    return token;
	} catch (Exception e) {
	    log.warn("[AUTH][{}] Authentication failed", usernameOrEmail, e);
	    throw new UnauthorizedLoginException();
	}
    }

    private void validateAuthRequest(AuthRequest authRequest) {
	String usernameOrEmail = authRequest.login();
	boolean isUsernameOrEmailBlank = StringUtils.isBlank(usernameOrEmail);
	if (isUsernameOrEmailBlank || StringUtils.isBlank(authRequest.password())) {
	    log.warn("[AUTH][{}] Missing user auth parameters", isUsernameOrEmailBlank ? "UNKNOWN" : usernameOrEmail);
	    throw new UserMissingAuthParameters();
	}
    }
}
