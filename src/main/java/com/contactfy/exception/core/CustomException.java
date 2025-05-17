package com.contactfy.exception.core;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionMessageKeyEnum messageKeyEnum;
    private final HttpStatus status;
    private final Object[] args;

    public CustomException(ExceptionMessageKeyEnum messageKey, HttpStatus status, Object[] args) {
	super(messageKey.getMsgCode());
	this.messageKeyEnum = messageKey;
	this.status = status;
	this.args = args;
    }
}
