package com.samuelDawid.medical_clinic.dto.institution;

import com.samuelDawid.medical_clinic.dto.CreateAddressCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record CreateInstitutionCommand(
        @Schema(description = "Name", example = "NewCare Clinic", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "List of doctors id",example = "[]")
        Set<Long> doctorsId,
        CreateAddressCommand address
) {
}
