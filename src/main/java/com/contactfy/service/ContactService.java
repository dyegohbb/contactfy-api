package com.contactfy.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.contactfy.api.dto.ContactDTO;
import com.contactfy.api.dto.ContactFilterDTO;
import com.contactfy.api.dto.core.ApiResponse;

@Service
public interface ContactService {

    ApiResponse<List<ContactDTO>> listAllContactsByUser(String loggedUsername, ContactFilterDTO filter, Pageable pageable);

    ApiResponse<ContactDTO> createContact(String loggedUsername, ContactDTO contactDTO);

    ApiResponse<ContactDTO> inactivateContact(String loggedUsername, String identifier);

    ApiResponse<ContactDTO> activateContact(String loggedUsername, String identifier);

    ApiResponse<ContactDTO> favoriteContact(String loggedUsername, String identifier);

    ApiResponse<ContactDTO> unfavoriteContact(String loggedUsername, String identifier);

    ApiResponse<ContactDTO> getContactByIdentifier(String loggedUsername, String identifier);

    ApiResponse<ContactDTO> updateContact(String name, String identifier, ContactDTO contactDTO);
}
