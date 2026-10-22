package models.institution;

import internalCommandsAndDtos.institution.CreateInstitutionInternalCommand;
import internalCommandsAndDtos.institution.PatchInstitutionInternalCommand;
import lombok.*;

import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionPOJO {
    private Long id;
    private String name;
    private AddressPOJO address;
    @ToString.Exclude
    private Set<Long> doctors;

    public static InstitutionPOJO create(CreateInstitutionInternalCommand command) {
        return InstitutionPOJO.builder()
                .id(null)
                .name(command.name())
                .address(AddressPOJO.create(command.address()))
                .build();
    }

    public void update(PatchInstitutionInternalCommand command) {
        if (Objects.nonNull(command.name())) {
            this.setName(command.name());
        }
        if (Objects.nonNull(command.city())) {
            this.getAddress().setCity(command.city());
        }
        if (Objects.nonNull(command.buildingNumber())) {
            this.getAddress().setBuildingNumber(command.buildingNumber());
        }
        if (Objects.nonNull(command.postCode())) {
            this.getAddress().setPostCode(command.postCode());
        }
        if (Objects.nonNull(command.street())) {
            this.getAddress().setStreet(command.street());
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
