package models;

import lombok.*;
import models.institution.InstitutionPOJO;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@ToString

public class DoctorPOJO {
    private Long id;
    private String medicalSpecialty;
    @ToString.Exclude
    private Set<InstitutionPOJO> institutions = new HashSet<>();
    @ToString.Exclude
    private UserPOJO user;

    public DoctorPOJO(String medicalSpecialty, UserPOJO user) {
        this.medicalSpecialty = medicalSpecialty;
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
        if (!(o instanceof DoctorPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
