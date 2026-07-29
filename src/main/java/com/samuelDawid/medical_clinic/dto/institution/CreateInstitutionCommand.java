package com.samuelDawid.medical_clinic.dto.institution;

import com.samuelDawid.medical_clinic.dto.CreateAddressCommand;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateInstitutionCommand(
        @Schema(description = "Name",example = "NewCare Clinic",requiredMode = Schema.RequiredMode.REQUIRED)
        String Name,
        CreateAddressCommand address
) {
}
