package com.samuelDawid.medical_clinic.model.institution;

import com.samuelDawid.medical_clinic.model.Doctor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "INSTITUTION")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String Name;
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "institution")
    private Set<Doctor> doctors = new HashSet<>();
}
