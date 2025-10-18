package com.example.usermgmt.dto;

import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UserPatchRequest")
public class UserPatchRequest {

    @Size(min = 2, max = 50)
    @Schema(example = "Ada")
    private String firstName;

    @Size(min = 2, max = 50)
    @Schema(example = "Lovelace")
    private String lastName;

    @Email
    @Size(max = 100)
    @Schema(example = "ada.lovelace@example.com")
    private String email;

    @Schema(example = "ADMIN")
    private UserRole role;

    @Schema(example = "INACTIVE")
    private UserStatus status;
}

