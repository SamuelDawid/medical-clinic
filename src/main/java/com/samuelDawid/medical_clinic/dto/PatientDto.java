package com.samuelDawid.medical_clinic.dto;


import java.time.LocalDate;


public record PatientDto(String email,
                         String firstName,
                         String lastName,
                         LocalDate birthDay,
                         String phoneNumber) {
}
