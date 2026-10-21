package internalCommands;

public record CreateAddressInternalCommand(
        String city,
        String postCode,
        String street,
        String buildingNumber
) {
}
