package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.service.UserPatcher;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicalSpecialty;
    @ManyToMany
    @JoinTable(
            name = "doctor_institution",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    @ToString.Exclude
    private Set<Institution> institutions;
    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    @ToString.Exclude
    private User user;

    public Doctor(String medicalSpecialty, Set<Institution> institutions, User user) {
        this.medicalSpecialty = medicalSpecialty;
        this.institutions = institutions;
        this.user = user;
    }

    public void update(@NonNull PatchDoctorCommand command, UserPatcher userPatcher) {
        if (command.medicalSpecialty() != null) {
            this.setMedicalSpecialty(command.medicalSpecialty());
        }
        if (command.user() != null) {
            userPatcher.apply(command.user(), this.getUser());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
