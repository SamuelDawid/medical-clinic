package com.samuelDawid.medical_clinic.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePasswordCommand(
        @Schema(description = "Account newPassword", example = "newPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String newPassword) {

}
