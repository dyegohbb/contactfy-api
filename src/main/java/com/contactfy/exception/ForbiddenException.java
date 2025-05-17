package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class ForbiddenException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.FORBIDDEN;
    private static HttpStatus status = HttpStatus.FORBIDDEN;

    public ForbiddenException(Object... args) {
	super(messageKey, status, args);
    }
    
}
