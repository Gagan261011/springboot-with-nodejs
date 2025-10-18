package com.example.usermgmt.mapper;

import com.example.usermgmt.dto.UserCreateRequest;
import com.example.usermgmt.dto.UserPatchRequest;
import com.example.usermgmt.dto.UserResponse;
import com.example.usermgmt.dto.UserUpdateRequest;
import com.example.usermgmt.entity.User;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntityFromDto(UserPatchRequest request, @MappingTarget User user);

    void updateEntityFromDto(UserUpdateRequest request, @MappingTarget User user);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}

