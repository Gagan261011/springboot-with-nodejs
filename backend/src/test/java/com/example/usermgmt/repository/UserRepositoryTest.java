package com.example.usermgmt.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.usermgmt.entity.User;
import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAll_withFilters_returnsMatchingUsers() {
        userRepository.save(User.builder()
                .firstName("Alice")
                .lastName("Wonderland")
                .email("alice@example.com")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build());

        userRepository.save(User.builder()
                .firstName("Bob")
                .lastName("Builder")
                .email("bob@example.com")
                .role(UserRole.MANAGER)
                .status(UserStatus.INACTIVE)
                .build());

        List<User> results = userRepository.findAll(UserSpecifications.withFilters("ali", null, UserStatus.ACTIVE));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Alice");
    }
}

