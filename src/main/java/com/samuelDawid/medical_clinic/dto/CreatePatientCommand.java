package com.samuelDawid.medical_clinic.dto;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CreatePatientCommand {
    String email;
    String password;
    String idCardNo;
    String firstName;
    String lastName;
    LocalDate birthDay;
    String phoneNumber;
}
