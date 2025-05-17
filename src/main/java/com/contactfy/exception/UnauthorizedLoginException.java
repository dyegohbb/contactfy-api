package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class UnauthorizedLoginException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.UNAUTHORIZED_LOGIN;
    private static HttpStatus status = HttpStatus.UNAUTHORIZED;

    public UnauthorizedLoginException() {
	super(messageKey, status, null);
    }
    
}
