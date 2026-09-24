package com.samuelDawid.medical_clinic.searchCriteria;

import io.swagger.v3.oas.annotations.media.Schema;

public record DoctorSearchCriteria(
        @Schema(description = "Medical speciality to filter doctors by", example = "CARDIOLOGY")
        String speciality
) {
}
