package com.samuelDawid.medical_clinic.dto.doctor;

import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record CreateDoctorCommand(
        @Schema(description = "Medical Speciality", example = "Anesthesiologists", requiredMode = Schema.RequiredMode.REQUIRED)
        String medicalSpecialty,
        CreateUserCommand user,
        @Schema(description = "Institution id",example = "[]")
        Set<Long> institutionId
) {
}
