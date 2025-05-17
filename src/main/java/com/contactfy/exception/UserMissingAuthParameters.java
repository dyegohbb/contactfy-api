package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class UserMissingAuthParameters extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.USER_MISSING_AUTH_PARAMETERS;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public UserMissingAuthParameters() {
	super(messageKey, status, null);
    }
    
}
