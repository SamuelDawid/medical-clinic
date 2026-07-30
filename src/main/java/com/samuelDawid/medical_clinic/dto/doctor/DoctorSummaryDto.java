package com.samuelDawid.medical_clinic.dto.doctor;

public record DoctorSummaryDto(
        String firstName,
        String lastName,
        String medicalSpecialty
) {
}
