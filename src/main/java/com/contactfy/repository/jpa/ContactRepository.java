package com.contactfy.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.contactfy.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END "
	    + "FROM Contact c WHERE c.cellphone = :cellphone AND c.user.identifier = :userIdentifier")
    boolean existsByCellphoneAndUser(String cellphone, String userIdentifier);

    Optional<Contact> findByIdentifier(String identifier);

}
