package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;


public class DuplicatedContactCellphoneException extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.DUPLICATED_CONTACT_CELLPHONE;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public DuplicatedContactCellphoneException(Object... args) {
	super(messageKey, status, args);
    }
}
