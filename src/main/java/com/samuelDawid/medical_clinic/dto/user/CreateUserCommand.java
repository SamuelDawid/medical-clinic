package com.samuelDawid.medical_clinic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserCommand(
        @Schema(description = "userName", example = "JohnDoe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String userName,
        @Schema(description = "password", example = "password132",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}
