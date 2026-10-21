package models.institution;

import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@ToString
public class InstitutionPOJO {
    private Long id;
    private String name;
    private AddressPOJO address;
    @ToString.Exclude
    private Set<Doctor> doctors = new HashSet<>();

    public InstitutionPOJO(String name, AddressPOJO address, Set<Doctor> doctors) {
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
        if (!(o instanceof InstitutionPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
