package com.contactfy.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDTO {

    private String identifier;
    private String name;
    private String email;
    private String cellphone;
    private String phone;
    private Boolean favorite;
    private Boolean active;
    private LocalDateTime createdAt;
    private UserDTO userDTO;

}

