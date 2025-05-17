package com.contactfy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

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
import com.contactfy.service.impl.ContactServiceImpl;

@SuppressWarnings("unchecked")
public class ContactServiceImplTest {

    @InjectMocks
    private ContactServiceImpl service;

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private UserService userService;
    @Mock
    private ContactAdapter contactAdapter;
    @Mock
    private ApiResponseAdapter responseAdapter;
    @Mock
    private ContactSpecification contactSpecification;

    @BeforeEach
    void setUp() {
	MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidInput_whenCreateContact_thenReturnsSuccessResponse() {
	String username = "testuser";
	User user = User.builder().identifier("user-123").build();
	ContactDTO dto = ContactDTO.builder().cellphone("99999999999").build();
	Contact contact = new Contact();
	ContactDTO resultDto = ContactDTO.builder().identifier("c1").build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.existsByCellphoneAndUser("99999999999", "user-123")).thenReturn(false);
	when(contactAdapter.toEntity(dto, user)).thenReturn(contact);
	when(contactRepository.save(contact)).thenReturn(contact);
	when(contactAdapter.toDTO(contact)).thenReturn(resultDto);
	when(responseAdapter.toSuccess(resultDto, MessageKeyEnum.CONTACT_CREATED, HttpStatus.OK))
		.thenReturn(new ApiResponse<ContactDTO>("1.0", MessageKeyEnum.CONTACT_CREATED.getMsgCode(), resultDto,
			HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.createContact(username, dto);

	assertEquals(resultDto, response.getContent());
	verify(contactRepository).save(contact);
    }

    @Test
    void givenDuplicatedCellphone_whenCreateContact_thenThrowsException() {
	String username = "testuser";
	User user = User.builder().identifier("user-123").build();
	ContactDTO dto = ContactDTO.builder().cellphone("99999999999").build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.existsByCellphoneAndUser("99999999999", "user-123")).thenReturn(true);

	assertThrows(DuplicatedContactCellphoneException.class, () -> service.createContact(username, dto));
    }

    @Test
    void givenInvalidOwner_whenInactivateContact_thenThrowsForbidden() {
	String username = "eviluser";
	User user = User.builder().identifier("unauthorized").build();
	Contact contact = new Contact();
	contact.setUser(User.builder().identifier("owner").build());

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier("id-1")).thenReturn(Optional.of(contact));

	assertThrows(ForbiddenException.class, () -> service.inactivateContact(username, "id-1"));
    }

    @Test
    void givenNonexistentContact_whenGetByIdentifier_thenThrowsNotFound() {
	when(contactRepository.findByIdentifier("nonexistent")).thenReturn(Optional.empty());

	assertThrows(ContactNotFoundException.class, () -> service.getContactByIdentifier("user", "nonexistent"));
    }

    @Test
    void givenValidInput_whenActivateContact_thenReturnsSuccessResponse() {
	String username = "testuser";
	User user = User.builder().identifier("u1").build();
	Contact contact = new Contact();
	contact.setIdentifier("c1");
	contact.setUser(user);
	ContactDTO dto = ContactDTO.builder().identifier("c1").build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier("c1")).thenReturn(Optional.of(contact));
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_ACTIVATED, HttpStatus.OK)).thenReturn(
		new ApiResponse<ContactDTO>("1.0", MessageKeyEnum.CONTACT_ACTIVATED.getMsgCode(), dto, HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.activateContact(username, "c1");

	assertEquals(dto, response.getContent());
	verify(contactRepository).save(contact);
    }

    @Test
    void givenFilterWithNoResults_whenListAllContactsByUser_thenReturnsNoContent() {
	String username = "user";
	User user = User.builder().identifier("u1").build();
	Pageable pageable = Pageable.ofSize(10);

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactSpecification.filter(any(), eq(user.getIdentifier()))).thenReturn((root, query, cb) -> null);
	when(contactRepository.findAll((Specification<Contact>) any(), eq(pageable)))
		.thenReturn(new PageImpl<>(List.of()));

	when(responseAdapter.toSuccess(eq(List.of()), eq(MessageKeyEnum.CONTACT_LISTED), eq(HttpStatus.NO_CONTENT)))
		.thenReturn(new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_LISTED.getMsgCode(), List.of(),
			HttpStatus.NO_CONTENT));

	ApiResponse<List<ContactDTO>> response = service.listAllContactsByUser(username,
		new ContactFilterDTO(null, null, null, null, null, null, null, null, null), pageable);

	assertEquals(0, response.getContent().size());
	assertEquals("1.0", response.getApiVersion());
	assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void givenInvalidDateRange_whenListAllContactsByUser_thenThrowsException() {
	ContactFilterDTO filter = new ContactFilterDTO(null, null, null, null, null, null, null,
		LocalDateTime.of(2024, 5, 1, 0, 0), LocalDateTime.of(2024, 5, 10, 0, 0));
	assertThrows(ContactFilterEndDateBeforeStartDate.class,
		() -> service.listAllContactsByUser("user", filter, Pageable.unpaged()));
    }

    @Test
    void givenValidIdentifier_whenGetContactByIdentifier_thenReturnsDTO() {
	String username = "user";
	String identifier = "c1";
	User user = User.builder().identifier("u1").build();
	Contact contact = new Contact();
	contact.setUser(user);
	contact.setIdentifier(identifier);
	ContactDTO dto = ContactDTO.builder().identifier(identifier).build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_RETRIEVED, HttpStatus.OK)).thenReturn(
		new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_RETRIEVED.getMsgCode(), dto, HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.getContactByIdentifier(username, identifier);

	assertEquals(dto, response.getContent());
	assertEquals("1.0", response.getApiVersion());
    }

    @Test
    void givenValidInput_whenInactivateContact_thenReturnsSuccessResponse() {
	String username = "user";
	String identifier = "c1";
	User user = User.builder().identifier("u1").build();
	Contact contact = new Contact();
	contact.setUser(user);
	contact.setIdentifier(identifier);
	ContactDTO dto = ContactDTO.builder().identifier(identifier).build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_INACTIVATED, HttpStatus.OK)).thenReturn(
		new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_INACTIVATED.getMsgCode(), dto, HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.inactivateContact(username, identifier);

	assertEquals(dto, response.getContent());
	verify(contactRepository).save(contact);
    }

    @Test
    void givenValidInput_whenFavoriteContact_thenReturnsSuccessResponse() {
	String username = "user";
	String identifier = "c1";
	User user = User.builder().identifier("u1").build();
	Contact contact = new Contact();
	contact.setUser(user);
	contact.setIdentifier(identifier);
	ContactDTO dto = ContactDTO.builder().identifier(identifier).build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_MARKED_FAVORITE, HttpStatus.OK)).thenReturn(
		new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_MARKED_FAVORITE.getMsgCode(), dto, HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.favoriteContact(username, identifier);

	assertEquals(dto, response.getContent());
	verify(contactRepository).save(contact);
    }

    @Test
    void givenValidInput_whenUnfavoriteContact_thenReturnsSuccessResponse() {
	String username = "user";
	String identifier = "c1";
	User user = User.builder().identifier("u1").build();
	Contact contact = new Contact();
	contact.setUser(user);
	contact.setIdentifier(identifier);
	ContactDTO dto = ContactDTO.builder().identifier(identifier).build();

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_UNFAVORITED, HttpStatus.OK)).thenReturn(
		new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_UNFAVORITED.getMsgCode(), dto, HttpStatus.OK));

	ApiResponse<ContactDTO> response = service.unfavoriteContact(username, identifier);

	assertEquals(dto, response.getContent());
	verify(contactRepository).save(contact);
    }

    @Test
    void givenInvalidOwner_whenChangeContactFavorite_thenThrowsForbidden() {
	String username = "user";
	String identifier = "c1";
	User user = User.builder().identifier("u1").build();
	User owner = User.builder().identifier("owner").build();
	Contact contact = new Contact();
	contact.setIdentifier(identifier);
	contact.setUser(owner);

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));

	assertThrows(ForbiddenException.class, () -> service.favoriteContact(username, identifier));
    }

    @Test
    void givenUnknownIdentifier_whenChangeContactFavorite_thenThrowsNotFound() {
	when(contactRepository.findByIdentifier("c1")).thenReturn(Optional.empty());

	assertThrows(ContactNotFoundException.class, () -> service.favoriteContact("user", "c1"));
    }

    @Test
    void givenPaginatedResultWithNextPage_whenListAllContactsByUser_thenHasNextPageTrue() {
	String username = "user";
	User user = User.builder().identifier("u1").build();
	Pageable pageable = Pageable.ofSize(1);
	Contact contact = new Contact();
	ContactDTO dto = ContactDTO.builder().identifier("c1").build();
	PageImpl<Contact> page = new PageImpl<>(List.of(contact), pageable, 2);

	when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
	when(contactSpecification.filter(any(), eq(user.getIdentifier()))).thenReturn((root, query, cb) -> null);
	when(contactRepository.findAll((Specification<Contact>) any(), eq(pageable))).thenReturn(page);
	when(contactAdapter.toDTO(contact)).thenReturn(dto);
	when(responseAdapter.toSuccess(any(), eq(MessageKeyEnum.CONTACT_LISTED), eq(HttpStatus.OK)))
		.thenAnswer(invocation -> {
		    ApiResponse<List<ContactDTO>> r = new ApiResponse<>("1.0",
			    MessageKeyEnum.CONTACT_LISTED.getMsgCode(), List.of(dto), HttpStatus.OK);
		    return r;
		});

	ApiResponse<List<ContactDTO>> response = service.listAllContactsByUser(username,
		new ContactFilterDTO(null, null, null, null, null, null, null, null, null), pageable);

	assertEquals(true, response.getHasNextPage());
    }
    
    @Test
    void givenValidInput_whenUpdateContact_thenReturnsSuccessResponse() {
        String username = "user";
        String identifier = "c1";
        User user = User.builder().identifier("u1").build();
        Contact contact = new Contact();
        contact.setUser(user);
        contact.setIdentifier(identifier);
        ContactDTO dto = ContactDTO.builder()
            .identifier(identifier)
            .name("Updated Name")
            .email("new@email.com")
            .cellphone("12345678901")
            .phone("123456789")
            .build();

        when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
        when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
        when(contactAdapter.toDTO(contact)).thenReturn(dto);
        when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_UPDATED, HttpStatus.OK))
            .thenReturn(new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_UPDATED.getMsgCode(), dto, HttpStatus.OK));

        ApiResponse<ContactDTO> response = service.updateContact(username, identifier, dto);

        assertEquals(dto, response.getContent());
        verify(contactRepository).save(contact);
    }
    
    @Test
    void givenInvalidUser_whenUpdateContact_thenThrowsForbiddenException() {
        String username = "user";
        String identifier = "c1";
        User logged = User.builder().identifier("logged").build();
        User owner = User.builder().identifier("owner").build();
        Contact contact = new Contact();
        contact.setUser(owner);

        when(userService.getUserByUsernameOrEmail(username)).thenReturn(logged);
        when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));

        assertThrows(ForbiddenException.class, () -> service.updateContact(username, identifier, new ContactDTO()));
    }

    @Test
    void givenNonexistentContact_whenUpdateContact_thenThrowsNotFoundException() {
        when(contactRepository.findByIdentifier("id")).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> service.updateContact("user", "id", new ContactDTO()));
    }

    @Test
    void givenBlankFields_whenUpdateContact_thenDoesNotOverrideExistingValues() {
        String username = "user";
        String identifier = "c1";
        User user = User.builder().identifier("u1").build();
        Contact contact = new Contact();
        contact.setUser(user);
        contact.setIdentifier(identifier);
        contact.setName("Old Name");
        contact.setEmail("old@email.com");

        ContactDTO dto = ContactDTO.builder()
            .identifier(identifier)
            .name(" ")
            .email(null)
            .cellphone("")
            .phone("   ")
            .build();

        when(userService.getUserByUsernameOrEmail(username)).thenReturn(user);
        when(contactRepository.findByIdentifier(identifier)).thenReturn(Optional.of(contact));
        when(contactAdapter.toDTO(contact)).thenReturn(dto);
        when(responseAdapter.toSuccess(dto, MessageKeyEnum.CONTACT_UPDATED, HttpStatus.OK))
            .thenReturn(new ApiResponse<>("1.0", MessageKeyEnum.CONTACT_UPDATED.getMsgCode(), dto, HttpStatus.OK));

        ApiResponse<ContactDTO> response = service.updateContact(username, identifier, dto);

        assertEquals(dto, response.getContent());
        assertEquals("Old Name", contact.getName());
        assertEquals("old@email.com", contact.getEmail());
    }

}
