package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;


public class UserNotFoundException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.USER_NOT_FOUND;
    private static HttpStatus status = HttpStatus.NOT_FOUND;

    public UserNotFoundException(Object... args) {
	super(messageKey, status, args);
    }

}
