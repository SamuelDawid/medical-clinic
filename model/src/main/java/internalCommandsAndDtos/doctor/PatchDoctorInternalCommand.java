package internalCommandsAndDtos.doctor;

import internalCommandsAndDtos.user.PatchUserInternalCommand;

public record PatchDoctorInternalCommand(
        String medicalSpecialty,
        PatchUserInternalCommand user
) {
}
