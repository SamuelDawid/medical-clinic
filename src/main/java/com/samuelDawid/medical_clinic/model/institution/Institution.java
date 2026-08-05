package com.samuelDawid.medical_clinic.model.institution;

import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.model.Doctor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "INSTITUTION")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "address_id",referencedColumnName = "id")
    private Address address;

    @ManyToMany(mappedBy = "institutions")
    @ToString.Exclude
    private Set<Doctor> doctors = new HashSet<>();

    public void update(@NonNull PatchInsitutionCommand command) {
        if (command.name() != null) {
            this.setName(command.name());
        }
        if (command.city() != null) {
            this.getAddress().setCity(command.city());
        }
        if (command.buildingNumber() != null) {
            this.getAddress().setBuildingNumber(command.buildingNumber());
        }
        if (command.postCode() != null) {
            this.getAddress().setPostCode(command.postCode());
        }
        if (command.street() != null) {
            this.getAddress().setStreet(command.street());
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || effectiveClassOf(this) != effectiveClassOf(o)) {
            return false;
        }
        Institution other = (Institution) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public final int hashCode() {
        return effectiveClassOf(this).hashCode();
    }

    private static Class<?> effectiveClassOf(Object entity) {
        return entity instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : entity.getClass();
    }
}
