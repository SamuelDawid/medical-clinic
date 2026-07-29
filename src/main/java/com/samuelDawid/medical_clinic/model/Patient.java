package com.samuelDawid.medical_clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToOne(cascade = CascadeType.ALL,optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id",unique = true)
    private User user;
}
