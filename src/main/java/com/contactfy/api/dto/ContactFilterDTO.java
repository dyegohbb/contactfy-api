package com.contactfy.api.dto;

import java.time.LocalDateTime;

public record ContactFilterDTO(
    String identifier,
    String name,
    String email,
    String cellphone,
    String phone,
    Boolean favorite,
    Boolean active,
    LocalDateTime createdAfter,
    LocalDateTime createdBefore
) {}

