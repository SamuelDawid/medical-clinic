package internalCommandsAndDtos.institution;

public record PatchAddressInternalCommand(
        String city,
        String postCode,
        String street,
        String buildingNumber
) {
}
