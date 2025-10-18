package com.example.usermgmt.dto;

import com.example.usermgmt.entity.UserRole;
import com.example.usermgmt.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User")
public class UserResponse {

    @Schema(description = "User identifier", example = "b7bdbd3c-0e46-4b7a-9ff0-5ad54a3f7701")
    private UUID id;

    @Schema(example = "Ada")
    private String firstName;

    @Schema(example = "Lovelace")
    private String lastName;

    @Schema(example = "ada.lovelace@example.com")
    private String email;

    @Schema(example = "ADMIN")
    private UserRole role;

    @Schema(example = "ACTIVE")
    private UserStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

