package com.samuelDawid.medical_clinic.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
//@Entity
public class Patient {
    String email;
    String password;
    String idCardNo;
    String firstName;
    String lastName;
    LocalDate birthDay;
    String phoneNumber;
    // public Patient(){}

    public Patient(String email, String password, String idCardNo, String firstName, String lastName, LocalDate birthDay, String phoneNumber) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);
        Objects.requireNonNull(idCardNo);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(phoneNumber);
        if (birthDay.isAfter(LocalDate.now())) throw new IllegalArgumentException("Invalid birth date");
        this.email = email;
        this.password = password;
        this.idCardNo = idCardNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
    }
}
