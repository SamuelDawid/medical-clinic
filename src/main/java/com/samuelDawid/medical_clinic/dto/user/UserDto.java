package com.samuelDawid.medical_clinic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDto(
        @Schema(description = "user id", example = "123")
        Long id,
        @Schema(description = "userName", example = "JohnDoe")
        String userName) {
}
