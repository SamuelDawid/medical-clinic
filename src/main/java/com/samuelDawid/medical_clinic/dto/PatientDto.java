package com.samuelDawid.medical_clinic.dto;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class PatientDto {
    String email;
    String firstName;
    String lastName;
    LocalDate birthDay;
    String phoneNumber;
}
