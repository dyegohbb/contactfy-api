package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class DuplicatedUserException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.DUPLICATED_USERNAME_EMAIL;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public DuplicatedUserException(Object... args) {
	super(messageKey, status, args);
    }
    
}
