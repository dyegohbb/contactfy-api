package com.contactfy.adapter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.contactfy.api.dto.ContactDTO;
import com.contactfy.entity.Contact;
import com.contactfy.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContactAdapter {
    
    private final UserAdapter userAdapter;

    public ContactDTO toDTO(Contact contact) {
	if (contact == null) {
	    return null;
	}

	return ContactDTO.builder()
		.identifier(contact.getIdentifier())
		.name(contact.getName())
		.email(contact.getEmail())
		.cellphone(contact.getCellphone())
		.phone(contact.getPhone())
		.favorite(contact.getFavorite())
		.active(contact.getActive())
		.createdAt(contact.getCreatedAt())
		.userDTO(userAdapter.toDTO(contact.getUser()))
		.build();
    }


    public Contact toEntity(ContactDTO contactDTO, User user) {
	if (contactDTO == null) {
	    return null;
	}

	return Contact.builder()
		.identifier(UUID.randomUUID().toString())
		.name(contactDTO.getName())
		.email(contactDTO.getEmail())
		.cellphone(contactDTO.getCellphone())
		.phone(contactDTO.getPhone())
		.favorite(contactDTO.getFavorite() == null ? true : contactDTO.getFavorite())
		.active(contactDTO.getActive() == null ? true : contactDTO.getActive())
		.createdAt(LocalDateTime.now())
		.user(user)
		.build();
    }


}
