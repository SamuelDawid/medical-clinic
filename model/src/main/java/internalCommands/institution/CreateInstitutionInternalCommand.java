package internalCommands.institution;


public record CreateInstitutionInternalCommand(
        String name,
        CreateAddressInternalCommand address
) {
}
