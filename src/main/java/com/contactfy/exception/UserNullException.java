package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class UserNullException extends CustomException {
    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.USER_NULL;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public UserNullException() {
	super(messageKey, status, null);
    }
}
