package com.samuelDawid.medical_clinic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserCommand(
        @Schema(description = "firstName", example = "John",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "lastName", example = "Doe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "email", example = "JohnDoe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "password", example = "password132",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}
