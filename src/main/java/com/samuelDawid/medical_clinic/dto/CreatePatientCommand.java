package com.samuelDawid.medical_clinic.dto;

import java.time.LocalDate;

public record CreatePatientCommand(String email,
                                   String password,
                                   String idCardNo,
                                   String firstName,
                                   String lastName,
                                   LocalDate birthDay,
                                   String phoneNumber) {
    public CreatePatientCommand(String email, String password, String idCardNo, String firstName, String lastName, LocalDate birthDay, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.idCardNo = idCardNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String idCardNo() {
        return idCardNo;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public LocalDate birthDay() {
        return birthDay;
    }

    @Override
    public String phoneNumber() {
        return phoneNumber;
    }
}


