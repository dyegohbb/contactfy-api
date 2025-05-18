package com.contactfy.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.contactfy.adapter.ApiResponseAdapter;
import com.contactfy.adapter.ContactAdapter;
import com.contactfy.api.dto.ContactDTO;
import com.contactfy.api.dto.ContactFilterDTO;
import com.contactfy.api.dto.core.ApiResponse;
import com.contactfy.common.MessageKeyEnum;
import com.contactfy.entity.Contact;
import com.contactfy.entity.User;
import com.contactfy.exception.ContactFilterEndDateBeforeStartDate;
import com.contactfy.exception.ContactNotFoundException;
import com.contactfy.exception.DuplicatedContactCellphoneException;
import com.contactfy.exception.ForbiddenException;
import com.contactfy.repository.jpa.ContactRepository;
import com.contactfy.repository.specification.ContactSpecification;
import com.contactfy.service.ContactService;
import com.contactfy.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService{
    
    private final ContactSpecification contactSpecification;
    private final ContactRepository contactRepository;
    private final UserService userService;
    private final ContactAdapter contactAdapter;
    private final ApiResponseAdapter responseAdapter;
    
    @Override
    public ApiResponse<List<ContactDTO>> listAllContactsByUser(String loggedUsername, ContactFilterDTO filter,
	    Pageable pageable) {
	
	validateContractFilter(filter);
	
	log.info("[CONTACT][LIST][{}] Retrieving user for list contacts", loggedUsername);
        User user = userService.getUserByUsernameOrEmail(loggedUsername);
        
        final Specification<Contact> specificationFilter = contactSpecification.filter(filter, user.getIdentifier());
        
        Page<Contact> contactPage = contactRepository.findAll(specificationFilter, pageable);
	List<ContactDTO> contacts = contactPage.stream().map(contactAdapter::toDTO).toList();
        
	HttpStatus httpStatus;
	if (contacts.isEmpty()) {
	    httpStatus = HttpStatus.NO_CONTENT;
            log.info("[CONTACT][LIST][{}] No contacts found for user on page {}, page size {}", loggedUsername, pageable.getPageNumber());
        } else {
            httpStatus = HttpStatus.OK;
            log.info("[CONTACT][LIST][{}] Successfully retrieved {} contacts on page {}, page size {}", loggedUsername, contacts.size(), pageable.getPageNumber());
        }
	
	ApiResponse<List<ContactDTO>> success = responseAdapter.toSuccess(contacts, MessageKeyEnum.CONTACT_LISTED,
		httpStatus);

        success.setHasNextPage(contactPage.hasNext());
        return success;
    }
    
    private void validateContractFilter(ContactFilterDTO filter) {
	if (filter.createdAfter() != null && filter.createdBefore() != null) {
	    if (filter.createdAfter().isBefore(filter.createdBefore())) {
		throw new ContactFilterEndDateBeforeStartDate();
	    }
	}
    }

    @Override
    public ApiResponse<ContactDTO> createContact(String loggedUsername, ContactDTO contactDTO) {
	
	String cellphone = contactDTO.getCellphone();
	
	log.info("[CONTACT][CREATE][{}] Retrieving user for contact contact creation with cellphone: {}", loggedUsername, cellphone);
	User user = userService.getUserByUsernameOrEmail(loggedUsername);
	
	if (contactRepository.existsByCellphoneAndUser(cellphone, user.getIdentifier())) {
	    log.warn("[CONTACT][CREATE][{}] Contact creation failed - Duplicate cellphone found: {}", loggedUsername, cellphone);
	    throw new DuplicatedContactCellphoneException(cellphone);
	}

	Contact savedContact = contactRepository.save(contactAdapter.toEntity(contactDTO, user));

	log.info("[CONTACT][CREATE][{}] Contact with cellphone '{}' successfully created", loggedUsername, cellphone);
	return responseAdapter.toSuccess(contactAdapter.toDTO(savedContact), MessageKeyEnum.CONTACT_CREATED, HttpStatus.OK);
    }

    @Override
    public ApiResponse<ContactDTO> inactivateContact(String loggedUsername, String identifier) {
	return changeContactActivation(loggedUsername, identifier, false);
    }

    @Override
    public ApiResponse<ContactDTO> activateContact(String loggedUsername, String identifier) {
	return changeContactActivation(loggedUsername, identifier, true);
    }
    
    private ApiResponse<ContactDTO> changeContactActivation(String loggedUsername, String identifier, boolean activated) {
	log.info("[CONTACT][INACTIVATE][{}] Starting inactivation process for contact with identifier: {}", loggedUsername, identifier);
        User user = userService.getUserByUsernameOrEmail(loggedUsername);

        Contact contact = contactRepository.findByIdentifier(identifier)
            .orElseThrow(() -> {
                log.error("[CONTACT][INACTIVATE][{}] Contact with identifier {} not found", loggedUsername, identifier);
                return new ContactNotFoundException(identifier);
            });

        if (!contact.getUser().getIdentifier().equals(user.getIdentifier())) {
            log.warn("[CONTACT][INACTIVATE][{}] User is not authorized to inactivate contact with identifier {}", loggedUsername, identifier);
            throw new ForbiddenException("Inactivate contact with identifier: " + identifier);
        }

        contact.setActive(activated);

        if(!activated) {
            contact.setFavorite(false);
        }
        
        contactRepository.save(contact);

        log.info("[CONTACT][INACTIVATE][{}] Contact with identifier {} successfully inactivated.", loggedUsername, identifier);
        
        MessageKeyEnum message = activated ? MessageKeyEnum.CONTACT_ACTIVATED : MessageKeyEnum.CONTACT_INACTIVATED;
        
        return responseAdapter.toSuccess(contactAdapter.toDTO(contact), message, HttpStatus.OK);
    }
    
    @Override
    public ApiResponse<ContactDTO> favoriteContact(String loggedUsername, String identifier) {
        return changeContactFavorite(loggedUsername, identifier, true);
    }

    @Override
    public ApiResponse<ContactDTO> unfavoriteContact(String loggedUsername, String identifier) {
        return changeContactFavorite(loggedUsername, identifier, false);
    }

    private ApiResponse<ContactDTO> changeContactFavorite(String loggedUsername, String identifier, boolean favorite) {
	    log.info("[CONTACT][FAVORITE][{}] Starting {} process for contact with identifier: {}", 
	        loggedUsername, favorite ? "favorite" : "unfavorite", identifier);

	    User user = userService.getUserByUsernameOrEmail(loggedUsername);

	    Contact contact = contactRepository.findByIdentifier(identifier)
	        .orElseThrow(() -> {
	            log.error("[CONTACT][FAVORITE][{}] Contact with identifier {} not found", loggedUsername, identifier);
	            return new ContactNotFoundException(identifier);
	        });

	    if (!contact.getUser().getIdentifier().equals(user.getIdentifier())) {
	        log.warn("[CONTACT][FAVORITE][{}] User is not authorized to update favorite flag on contact with identifier {}", loggedUsername, identifier);
	        throw new ForbiddenException("Change favorite on contact with identifier: " + identifier);
	    }

	    contact.setFavorite(favorite);

	    contactRepository.save(contact);

	    log.info("[CONTACT][FAVORITE][{}] Contact with identifier {} successfully marked as {}", 
	        loggedUsername, identifier, favorite ? "favorite" : "not favorite");

	    MessageKeyEnum message = favorite ? MessageKeyEnum.CONTACT_MARKED_FAVORITE : MessageKeyEnum.CONTACT_UNFAVORITED;
	    return responseAdapter.toSuccess(contactAdapter.toDTO(contact), message, HttpStatus.OK);
	}

    @Override
    public ApiResponse<ContactDTO> getContactByIdentifier(String loggedUsername, String identifier) {
        log.info("[CONTACT][GET][{}] Getting contact for user with identifier: {}", loggedUsername, identifier);
        User user = userService.getUserByUsernameOrEmail(loggedUsername);
        Contact contact = contactRepository.findByIdentifier(identifier)
            .orElseThrow(() -> {
                log.info("[CONTACT][GET][{}] No contact found with identifier: {}", loggedUsername, identifier);
                throw new ContactNotFoundException(identifier);
            });

        if (!contact.getUser().getIdentifier().equals(user.getIdentifier())) {
            log.warn("[CONTACT][GET][{}] User is not authorized to access contact with identifier {}", loggedUsername, identifier);
            throw new ForbiddenException("Get contact with identifier: " + identifier);
        }

        log.info("[CONTACT][GET][{}] Successfully retrieved contact with identifier: {}", loggedUsername, identifier);
        return responseAdapter.toSuccess(contactAdapter.toDTO(contact), MessageKeyEnum.CONTACT_RETRIEVED, HttpStatus.OK);
    }

    @Override
    public ApiResponse<ContactDTO> updateContact(String loggedUsername, String contactIdentifier, ContactDTO contactDTO) {

        log.info("[CONTACT][UPDATE][{}] Starting update process for contact with identifier: {}", loggedUsername, contactIdentifier);

        User user = userService.getUserByUsernameOrEmail(loggedUsername);

        Contact contact = contactRepository.findByIdentifier(contactIdentifier)
            .orElseThrow(() -> {
                log.error("[CONTACT][UPDATE][{}] Contact with identifier {} not found", loggedUsername, contactIdentifier);
                return new ContactNotFoundException(contactIdentifier);
            });

        if (!contact.getUser().getIdentifier().equals(user.getIdentifier())) {
            log.warn("[CONTACT][UPDATE][{}] User is not authorized to update contact with identifier {}", loggedUsername, contactIdentifier);
            throw new ForbiddenException("Update contact with identifier: " + contactIdentifier);
        }

        updateContactFields(contact, contactDTO);

        contactRepository.save(contact);

        log.info("[CONTACT][UPDATE][{}] Contact with identifier {} successfully updated.", loggedUsername, contact.getIdentifier());

        return responseAdapter.toSuccess(contactAdapter.toDTO(contact), MessageKeyEnum.CONTACT_UPDATED, HttpStatus.OK);
    }

    private void updateContactFields(Contact contact, ContactDTO contactDTO) {
	if (contactDTO.getName() != null && !contactDTO.getName().isBlank()) {
	    contact.setName(contactDTO.getName());
	}

	if (contactDTO.getEmail() != null && !contactDTO.getEmail().isBlank()) {
	    contact.setEmail(contactDTO.getEmail());
	}

	if (contactDTO.getCellphone() != null && !contactDTO.getCellphone().isBlank()) {
	    contact.setCellphone(contactDTO.getCellphone());
	}

	if (contactDTO.getPhone() != null && !contactDTO.getPhone().isBlank()) {
	    contact.setPhone(contactDTO.getPhone());
	}
	
	if (contactDTO.getActive() != null) {
	    contact.setActive(contactDTO.getActive());
	}
	
	if (contactDTO.getFavorite() != null) {
	    contact.setFavorite(contactDTO.getFavorite());
	}
    }

}
