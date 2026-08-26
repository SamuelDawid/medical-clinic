package com.samuelDawid.medical_clinic.dto.doctor;

import com.samuelDawid.medical_clinic.dto.user.PatchUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import org.mapstruct.Mapping;

public record PatchDoctorCommand(
        @Schema(description = "Medical Speciality", example = "Anesthesiologists", requiredMode = Schema.RequiredMode.REQUIRED)
        String medicalSpecialty,
        @Mapping(target = "user")
        PatchUserCommand user
) {
}
