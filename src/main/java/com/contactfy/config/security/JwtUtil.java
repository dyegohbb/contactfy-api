package com.contactfy.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.contactfy.adapter.ApiResponseAdapter;
import com.contactfy.api.dto.TokenDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.common.MessageKeyEnum;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;
    
    private final ApiResponseAdapter apiResponseAdapter;
    
    public ApiResponse<TokenDTO> generateToken(String username) {
        Date expiresAt = new Date(System.currentTimeMillis() + expiration);
	String token = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secret));
	
	TokenDTO tokenDTO = TokenDTO.builder()
		.expiresAt(expiresAt)
		.token(token).build();
	
	return apiResponseAdapter.toSuccess(tokenDTO, MessageKeyEnum.TOKEN_GENERATED, HttpStatus.OK);
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
    
}
