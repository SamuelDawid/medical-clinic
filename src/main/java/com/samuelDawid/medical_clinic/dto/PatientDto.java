package com.samuelDawid.medical_clinic.dto;

import lombok.Getter;

import java.time.LocalDate;


public record PatientDto( String email,
        String firstName,
        String lastName,
        LocalDate birthDay,
        String phoneNumber) {

    public PatientDto(String email, String firstName, String lastName, LocalDate birthDay, String phoneNumber) {
        this.email = email;
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
