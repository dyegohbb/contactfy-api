package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class ContactNotFoundException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.CONTACT_NOT_FOUND;
    private static HttpStatus status = HttpStatus.NOT_FOUND;

    public ContactNotFoundException(Object... args) {
	super(messageKey, status, args);
    }

}
