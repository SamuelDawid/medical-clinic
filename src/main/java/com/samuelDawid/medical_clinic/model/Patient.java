package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.service.UserPatcher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PATIENT")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String idCardNo;
    private LocalDate birthDay;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    public void update(@NonNull PatchPatientCommand patientCommand, UserPatcher userPatcher) {
        if (patientCommand.idCardNo() != null) {
            this.setIdCardNo(patientCommand.idCardNo());
        }
        if (patientCommand.birthDay() != null) {
            this.setBirthDay(patientCommand.birthDay());
        }
        if (patientCommand.phoneNumber() != null) {
            this.setPhoneNumber(patientCommand.phoneNumber());
        }
        if (patientCommand.user() != null) {
            userPatcher.apply(patientCommand.user(), this.getUser());
        }
    }
}
