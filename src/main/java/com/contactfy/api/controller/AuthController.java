package com.contactfy.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contactfy.api.dto.AuthRequest;
import com.contactfy.api.dto.TokenDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ApiResponse<TokenDTO>> authenticate(@RequestBody AuthRequest authRequest) throws Exception {
        ApiResponse<TokenDTO> authenticate = authService.authenticate(authRequest);
        
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }
}
