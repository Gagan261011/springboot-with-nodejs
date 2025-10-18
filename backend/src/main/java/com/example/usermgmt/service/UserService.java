package com.example.usermgmt.service;

import com.example.usermgmt.dto.PageResponse;
import com.example.usermgmt.dto.UserCreateRequest;
import com.example.usermgmt.dto.UserPatchRequest;
import com.example.usermgmt.dto.UserResponse;
import com.example.usermgmt.dto.UserUpdateRequest;
import com.example.usermgmt.entity.User;
import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import com.example.usermgmt.exception.ConflictException;
import com.example.usermgmt.exception.ResourceNotFoundException;
import com.example.usermgmt.mapper.UserMapper;
import com.example.usermgmt.repository.UserRepository;
import com.example.usermgmt.repository.UserSpecifications;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("updatedAt"));
    private static final java.util.Set<String> ALLOWED_SORT_PROPERTIES = java.util.Set.of(
            "firstName", "lastName", "email", "role", "status", "createdAt", "updatedAt"
    );

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        ensureEmailUnique(request.getEmail(), null);
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(
            String search, UserRole role, UserStatus status, int page, int size, List<String> sortParams) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sortParams));
        Specification<User> specification = UserSpecifications.withFilters(search, role, status);
        Page<User> result = userRepository.findAll(specification, pageable);
        return PageResponse.<UserResponse>builder()
                .items(userMapper.toResponseList(result.getContent()))
                .page(result.getNumber())
                .size(result.getSize())
                .totalItems(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .sort(toSortStrings(result.getSort()))
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return userMapper.toResponse(findUserById(id));
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = findUserById(id);
        ensureEmailUnique(request.getEmail(), id);
        userMapper.updateEntityFromDto(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse patchUser(UUID id, UserPatchRequest request) {
        User user = findUserById(id);
        if (StringUtils.hasText(request.getEmail())) {
            ensureEmailUnique(request.getEmail(), id);
        }
        userMapper.patchEntityFromDto(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = findUserById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "User with id %s not found".formatted(id)));
    }

    private void ensureEmailUnique(String email, UUID idToExclude) {
        boolean exists = (idToExclude == null)
                ? userRepository.existsByEmailIgnoreCase(email)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(email, idToExclude);
        if (exists) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Email address is already in use");
        }
    }

    private Sort buildSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return DEFAULT_SORT;
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < sortParams.size(); i++) {
            String rawParam = sortParams.get(i);
            if (!StringUtils.hasText(rawParam)) {
                continue;
            }
            String property;
            String directionToken = null;

            String[] tokens = rawParam.split(",");
            if (tokens.length > 1) {
                property = tokens[0].trim();
                directionToken = tokens[1].trim();
            } else {
                property = rawParam.trim();
                if (i + 1 < sortParams.size()) {
                    String potentialDirection = sortParams.get(i + 1);
                    if (isDirectionToken(potentialDirection)) {
                        directionToken = potentialDirection.trim();
                        i++;
                    }
                }
            }

            if (!StringUtils.hasText(property)) {
                continue;
            }

            // Validate property against allowed list to avoid invalid or unexpected sort properties
            if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
                // skip invalid property
                continue;
            }

            Sort.Direction direction = parseDirection(directionToken);
            orders.add(new Sort.Order(direction, property));
        }
        return orders.isEmpty() ? DEFAULT_SORT : Sort.by(orders);
    }

    private List<String> toSortStrings(Sort sort) {
        List<String> sorts = new ArrayList<>();
        sort.stream()
                .forEach(order -> sorts.add(order.getProperty() + "," + order.getDirection().name().toLowerCase(Locale.ROOT)));
        return sorts;
    }

    private boolean isDirectionToken(String value) {
        return StringUtils.hasText(value)
                && ("asc".equalsIgnoreCase(value.trim()) || "desc".equalsIgnoreCase(value.trim()));
    }

    private Sort.Direction parseDirection(String token) {
        if (StringUtils.hasText(token) && "asc".equalsIgnoreCase(token.trim())) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }
}
