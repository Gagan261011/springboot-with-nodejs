package com.example.usermgmt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.usermgmt.dto.UserCreateRequest;
import com.example.usermgmt.dto.UserPatchRequest;
import com.example.usermgmt.dto.UserUpdateRequest;
import com.example.usermgmt.entity.User;
import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import com.example.usermgmt.exception.ConflictException;
import com.example.usermgmt.exception.ResourceNotFoundException;
import com.example.usermgmt.mapper.UserMapper;
import com.example.usermgmt.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private UserMapper userMapper;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void createUser_persistsAndReturnsResponse() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada@example.com");
        request.setRole(UserRole.ADMIN);
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
        OffsetDateTime now = OffsetDateTime.now();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.builder()
                    .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        });

        var response = userService.createUser(request);

        assertThat(response.getId()).isEqualTo(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        assertThat(response.getEmail()).isEqualTo("ada@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenEmailExists_throwsConflict() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFirstName("Grace");
        request.setLastName("Hopper");
        request.setEmail("grace@example.com");
        request.setRole(UserRole.MANAGER);
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email address is already in use");
    }

    @Test
    void updateUser_whenNotFound_throwsResourceNotFound() {
        UUID id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Alan");
        request.setLastName("Turing");
        request.setEmail("alan@example.com");
        request.setRole(UserRole.VIEWER);
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id");
    }

    @Test
    void patchUser_updatesSelectiveFields() {
        UUID id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UserPatchRequest request = new UserPatchRequest();
        request.setFirstName("Margaret");

        User existing = User.builder()
                .id(id)
                .firstName("Maggie")
                .lastName("Hamilton")
                .email("margaret@example.com")
                .role(UserRole.MANAGER)
                .status(UserStatus.ACTIVE)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now().minusDays(1))
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.patchUser(id, request);

        assertThat(response.getFirstName()).isEqualTo("Margaret");
        assertThat(response.getLastName()).isEqualTo("Hamilton");
    }

    @Test
    void getUsers_withSplitSortTokens_buildsSortCorrectly() {
        Page<User> page = new PageImpl<>(List.of(), PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt"))), 0);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var response = userService.getUsers(null, null, null, 0, 10, List.of("updatedAt", "desc"));

        verify(userRepository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Order.desc("updatedAt")));
        assertThat(response.getSort()).containsExactly("updatedAt,desc");
    }
}
