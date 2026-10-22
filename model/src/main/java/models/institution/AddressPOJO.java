package models.institution;

import internalCommandsAndDtos.institution.CreateAddressInternalCommand;
import internalCommandsAndDtos.institution.PatchAddressInternalCommand;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AddressPOJO {
    Long id;
    private String city;
    private String postCode;
    private String street;
    private String buildingNumber;

    public static AddressPOJO create(CreateAddressInternalCommand command) {
        return AddressPOJO.builder()
                .id(null)
                .city(command.city())
                .postCode(command.postCode())
                .street(command.street())
                .buildingNumber(command.buildingNumber())
                .build();
    }

    public void update(PatchAddressInternalCommand command) {
        if (Objects.nonNull(command.city())) {
            this.city = command.city();
        }
        if (Objects.nonNull(command.postCode())) {
            this.postCode = command.postCode();
        }
        if (Objects.nonNull(command.street())) {
            this.street = command.street();
        }
        if (Objects.nonNull(command.buildingNumber())) {
            this.buildingNumber = command.buildingNumber();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
