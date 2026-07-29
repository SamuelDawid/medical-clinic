package com.samuelDawid.medical_clinic.dto.patient;

import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import org.mapstruct.Mapping;

import java.time.LocalDate;

public record CreatePatientCommand(
        @Schema(description = "ID card number", example = "1005200004554",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String idCardNo,
        @Schema(description = "Birth date in ISO format (YYYY-MM-DD)", example = "2000-02-22",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate birthDay,
        @Schema(description = "Phone number", example = "+48 609567865",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber,
        @Mapping(target = "user",ignore = true)
        CreateUserCommand user
) {
}


