package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class UserMissingCreateParameters extends CustomException {
    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.USER_MISSING_CREATE_PARAMETERS;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public UserMissingCreateParameters(Object... args) {
   	super(messageKey, status, args);
       }
}
