package com.example.usermgmt.dto;

import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "UserUpdateRequest")
public class UserUpdateRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(example = "Ada")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(example = "Lovelace")
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(example = "ada.lovelace@example.com")
    private String email;

    @NotNull
    @Schema(example = "MANAGER")
    private UserRole role;

    @NotNull
    @Schema(example = "ACTIVE")
    private UserStatus status;
}

