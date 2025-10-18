package com.example.usermgmt.repository;

import com.example.usermgmt.entity.User;
import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> withFilters(String search, UserRole role, UserStatus status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String likePattern = "%" + search.trim().toLowerCase() + "%";
                Predicate firstNamePredicate = builder.like(builder.lower(root.get("firstName")), likePattern);
                Predicate lastNamePredicate = builder.like(builder.lower(root.get("lastName")), likePattern);
                Predicate emailPredicate = builder.like(builder.lower(root.get("email")), likePattern);
                predicates.add(builder.or(firstNamePredicate, lastNamePredicate, emailPredicate));
            }

            if (role != null) {
                predicates.add(builder.equal(root.get("role"), role));
            }

            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

