package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.model.institution.Institution;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicalSpecialty;
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",unique = true)
    User user;
}
