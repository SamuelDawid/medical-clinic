package com.samuelDawid.medical_clinic.model.institution;

import com.samuelDawid.medical_clinic.dto.institution.PatchInstitutionCommand;
import com.samuelDawid.medical_clinic.model.Doctor;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "INSTITUTION")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany(mappedBy = "institutions")
    @ToString.Exclude
    private Set<Doctor> doctors = new HashSet<>();

    public Institution(String name, Address address, Set<Doctor> doctors) {
        this.name = name;
        this.address = address;
        this.doctors = doctors;
    }

    public void update(@NonNull PatchInstitutionCommand command) {
        if (command.name() != null) {
            this.setName(command.name());
        }
        if (command.city() != null) {
            this.getAddress()
                    .setCity(command.city());
        }
        if (command.buildingNumber() != null) {
            this.getAddress()
                    .setBuildingNumber(command.buildingNumber());
        }
        if (command.postCode() != null) {
            this.getAddress()
                    .setPostCode(command.postCode());
        }
        if (command.street() != null) {
            this.getAddress()
                    .setStreet(command.street());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
