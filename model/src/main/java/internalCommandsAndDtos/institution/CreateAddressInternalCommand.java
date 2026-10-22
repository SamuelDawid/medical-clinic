package internalCommandsAndDtos.institution;

public record CreateAddressInternalCommand(
        String city,
        String postCode,
        String street,
        String buildingNumber
) {
}
