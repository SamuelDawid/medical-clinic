package com.samuelDawid.medical_clinic.dto.institution;

import com.samuelDawid.medical_clinic.dto.doctor.DoctorSummaryDto;

import java.util.Set;

public record InstitutionDoctorsDto(
        String institutionName,
        Set<DoctorSummaryDto> doctorsDto
) {
}
