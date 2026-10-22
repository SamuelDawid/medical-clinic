package internalCommands.doctor;

import internalCommands.user.PatchUserInternalCommand;
import org.mapstruct.Mapping;

public record PatchDoctorInternalCommand(
        String medicalSpecialty,
        @Mapping(target = "user")
        PatchUserInternalCommand user
) {
}
