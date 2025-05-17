package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class ContactMissingCreateParameters extends CustomException {
    
    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.CONTACT_MISSING_CREATE_PARAMETERS;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public ContactMissingCreateParameters(Object... args) {
	super(messageKey, status, args);
    }
}
