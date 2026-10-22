package internalCommandsAndDtos.doctor;

import internalCommandsAndDtos.user.PatchUserInternalCommand;
import org.mapstruct.Mapping;

public record PatchDoctorInternalCommand(
        String medicalSpecialty,
        @Mapping(target = "user")
        PatchUserInternalCommand user
) {
}
