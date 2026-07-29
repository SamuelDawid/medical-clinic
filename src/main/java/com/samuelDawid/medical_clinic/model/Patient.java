package com.samuelDawid.medical_clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapping;

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
    private String email;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL,optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
