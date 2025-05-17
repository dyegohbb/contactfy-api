package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class ContactNullException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.CONTACT_NULL;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public ContactNullException() {
	super(messageKey, status, null);
    }
    
}
