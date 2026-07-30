package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.model.institution.Institution;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicalSpecialty;
    @ManyToMany
    @JoinTable(
            name = "doctors",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    private Set<Institution> institutions;
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id",referencedColumnName = "id",unique = true)
    User user;
}
