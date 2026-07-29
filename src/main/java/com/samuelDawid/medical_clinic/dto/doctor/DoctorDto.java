package com.samuelDawid.medical_clinic.dto.doctor;

import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.user.UserDto;

public record DoctorDto(
        String medicalSpecialty,
        InstitutionDto institutionDto,
        UserDto userDto
) {
}
