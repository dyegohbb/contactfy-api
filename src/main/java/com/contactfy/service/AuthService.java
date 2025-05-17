package com.contactfy.service;

import com.contactfy.api.dto.AuthRequest;
import com.contactfy.api.dto.TokenDTO;
import com.contactfy.api.dto.core.ApiResponse;

public interface AuthService {
    ApiResponse<TokenDTO> authenticate(AuthRequest authRequest);
}
