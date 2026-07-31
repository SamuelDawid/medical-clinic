package com.samuelDawid.medical_clinic.dto.doctor;

import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.user.UserDto;

import java.util.Set;

public record DoctorDto(
        Long id,
        String medicalSpecialty,
        Set<InstitutionDto> institutionDto,
        UserDto userDto
) {
}
