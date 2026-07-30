package com.samuelDawid.medical_clinic.dto.institution;

import com.samuelDawid.medical_clinic.model.institution.Address;
import io.swagger.v3.oas.annotations.media.Schema;

public record InstitutionDto(
        @Schema(description = "id", example = "123")
        Long id,
        @Schema(description = "Name", example = "NewCare Clinic")
        String name,
        Address address
) {
}
