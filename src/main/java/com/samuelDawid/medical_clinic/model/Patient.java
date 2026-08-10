package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.service.UserPatcher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PATIENT")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String idCardNo;
    private LocalDate birthDay;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    @ToString.Exclude
    private User user;

    public Patient(String idCardNo, LocalDate birthDay, String phoneNumber, User user) {
        this.idCardNo = idCardNo;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
