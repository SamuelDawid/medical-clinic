package com.samuelDawid.medical_clinic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDto(
        @Schema(description = "user id", example = "123")
        Long id,
        @Schema(description = "firstName", example = "John",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "lastName", example = "Doe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "email", example = "JohnDoe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email
) {
}
