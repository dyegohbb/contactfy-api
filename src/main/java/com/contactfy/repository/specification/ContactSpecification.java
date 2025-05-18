package com.contactfy.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.contactfy.api.dto.ContactFilterDTO;
import com.contactfy.entity.Contact;

import jakarta.persistence.criteria.Predicate;

@Component
public class ContactSpecification {

    public Specification<Contact> filter(final ContactFilterDTO filter, final String userIdentifier) {
	return (root, query, cb) -> {
	    final List<Predicate> predicates = new ArrayList<>();

	    if (StringUtils.isNotBlank(filter.identifier())) {
		predicates.add(cb.equal(root.get("identifier"), filter.identifier()));
	    }

	    if (StringUtils.isNotBlank(filter.name())) {
		predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.name().toLowerCase() + "%"));
	    }

	    if (StringUtils.isNotBlank(filter.email())) {
		predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.email().toLowerCase() + "%"));
	    }

	    if (StringUtils.isNotBlank(filter.cellphone())) {
		predicates.add(cb.like(cb.lower(root.get("cellphone")), "%" + filter.cellphone().toLowerCase() + "%"));
	    }

	    if (StringUtils.isNotBlank(filter.phone())) {
		predicates.add(cb.like(cb.lower(root.get("phone")), "%" + filter.phone().toLowerCase() + "%"));
	    }

	    if (filter.favorite() != null) {
		predicates.add(cb.equal(root.get("favorite"), filter.favorite()));
	    }

	    if (filter.active() != null) {
		predicates.add(cb.equal(root.get("active"), filter.active()));
	    }

	    if (filter.createdAfter() != null && filter.createdBefore() != null) {
		predicates.add(cb.between(root.get("createdAt"), filter.createdAfter(), filter.createdBefore()));
	    } else if (filter.createdAfter() != null) {
		predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.createdAfter()));
	    } else if (filter.createdBefore() != null) {
		predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.createdBefore()));
	    }

	    predicates.add(cb.equal(root.get("user").get("identifier"), userIdentifier));

	    return cb.and(predicates.toArray(new Predicate[0]));
	};
    }
}
