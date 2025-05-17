package com.contactfy.exception;

import org.springframework.http.HttpStatus;

import com.contactfy.exception.core.CustomException;
import com.contactfy.exception.core.ExceptionMessageKeyEnum;

public class ContactFilterEndDateBeforeStartDate extends CustomException {

    private static ExceptionMessageKeyEnum messageKey = ExceptionMessageKeyEnum.CONTACT_FILTER_ENDDATE_BEFORE_STARTDATE;
    private static HttpStatus status = HttpStatus.BAD_REQUEST;

    public ContactFilterEndDateBeforeStartDate(Object... args) {
	super(messageKey, status, args);
    }
}
