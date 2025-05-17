package com.contactfy.exception.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessageKeyEnum {

    GENERIC_ERROR("system.generic_error"), 
    UNAUTHORIZED_LOGIN("auth.unauthorized.error"), 
    FORBIDDEN("forbidden.error"),
    DUPLICATED_USERNAME_EMAIL("user.duplicated_username_or_email.error"),
    USER_NOT_FOUND("user.not_found.error"),
    USER_MISSING_AUTH_PARAMETERS("user.missing_auth_parameters.error"),
    USER_NULL("user.null.error"),
    USER_MISSING_CREATE_PARAMETERS("user.missing_create_parameters.error"),
    DUPLICATED_CONTACT_CELLPHONE("contact.duplicated_cellphone.error"),
    CONTACT_MISSING_CREATE_PARAMETERS("contact.missing_create_parameters.error"),
    CONTACT_NULL("contact.null.error"),
    CONTACT_NOT_FOUND("contact.not_found.error"),
    CONTACT_FILTER_ENDDATE_BEFORE_STARTDATE("contact.filter_enddate_before_startdate.error");;

    private final String msgCode;

}
