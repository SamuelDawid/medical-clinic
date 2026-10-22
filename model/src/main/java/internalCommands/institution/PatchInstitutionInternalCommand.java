package internalCommands.institution;

public record PatchInstitutionInternalCommand(
        String name,
        String city,
        String postCode,
        String street,
        String buildingNumber
) {
}
