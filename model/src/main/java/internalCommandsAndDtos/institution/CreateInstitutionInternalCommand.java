package internalCommandsAndDtos.institution;


public record CreateInstitutionInternalCommand(
        String name,
        CreateAddressInternalCommand address
) {
}
