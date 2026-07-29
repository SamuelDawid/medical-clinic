package com.samuelDawid.medical_clinic.dto.doctor;

import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import org.mapstruct.Mapping;

public record CreateDoctorCommand(
        @Schema(description = "Medical Speciality", example = "Anesthesiologists", requiredMode = Schema.RequiredMode.REQUIRED)
        String medicalSpecialty,
        @Mapping(target = "institution", ignore = true)
        CreateInstitutionCommand institutionCommand
) {
}
