package com.contactfy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageKeyEnum {
    USER_CREATED("user.created"),
    USER_UPDATED("user.updated"),
    USER_DELETED("user.deleted"),
    CONTACT_CREATED("contact.created"),
    CONTACT_UPDATED("contact.updated"),
    CONTACT_INACTIVATED("contact.inactivated"),
    CONTACT_ACTIVATED("contact.activated"),
    CONTACT_LISTED("contact.listed"),
    CONTACT_RETRIEVED("contact.retrieved"),
    TOKEN_GENERATED("token.generated"), 
    CONTACT_MARKED_FAVORITE("contact.favorited"),
    CONTACT_UNFAVORITED("contact.unfavorited");

    private String msgCode;
}
