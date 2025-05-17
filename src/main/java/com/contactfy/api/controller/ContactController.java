package com.contactfy.api.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contactfy.api.dto.ContactDTO;
import com.contactfy.api.dto.ContactFilterDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.service.ContactService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {
    
    private final ContactService contactService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactDTO>>> listAll(@ParameterObject @ModelAttribute ContactFilterDTO filter, @ParameterObject Pageable pageable, Authentication authentication) {
	ApiResponse<List<ContactDTO>> contacts = contactService.listAllContactsByUser(authentication.getName(), filter, pageable);
	return new ResponseEntity<>(contacts, contacts.getStatus());
    }
    
    @GetMapping("/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> get(@PathVariable String identifier, Authentication authentication) {
	ApiResponse<ContactDTO> contacts = contactService.getContactByIdentifier(authentication.getName(), identifier);
	return new ResponseEntity<>(contacts, contacts.getStatus());
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ContactDTO>> createContact(@RequestBody ContactDTO contactDTO, Authentication authentication) {
        
	ApiResponse<ContactDTO> task = contactService.createContact(authentication.getName(), contactDTO);
	return new ResponseEntity<>(task, task.getStatus());
    }
    
    @PutMapping("/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> updateTask(@PathVariable String identifier, @RequestBody ContactDTO contactDTO,
	    Authentication authentication) {

	ApiResponse<ContactDTO> response = contactService.updateContact(authentication.getName(), identifier, contactDTO);

	return new ResponseEntity<>(response, response.getStatus());
    }
    
    @PatchMapping(path = "/inactivate/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> inactivateContact(@PathVariable String identifier, Authentication authentication) {
        
	ApiResponse<ContactDTO> task = contactService.inactivateContact(authentication.getName(), identifier);
	return new ResponseEntity<>(task, task.getStatus());
    }
    
    @PatchMapping(path = "/activate/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> activateContact(@PathVariable String identifier, Authentication authentication) {
        
	ApiResponse<ContactDTO> task = contactService.activateContact(authentication.getName(), identifier);
	return new ResponseEntity<>(task, task.getStatus());
    }
    
    @PatchMapping(path = "/unfavorite/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> unfavoriteContact(@PathVariable String identifier, Authentication authentication) {
        
	ApiResponse<ContactDTO> task = contactService.unfavoriteContact(authentication.getName(), identifier);
	return new ResponseEntity<>(task, task.getStatus());
    }
    
    @PatchMapping(path = "/favorite/{identifier}")
    public ResponseEntity<ApiResponse<ContactDTO>> favoriteContact(@PathVariable String identifier, Authentication authentication) {
        
	ApiResponse<ContactDTO> task = contactService.favoriteContact(authentication.getName(), identifier);
	return new ResponseEntity<>(task, task.getStatus());
    }

}
