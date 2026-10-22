package internalCommandsAndDtos.patient;

import internalCommandsAndDtos.user.PatchUserInternalCommand;

import java.time.LocalDate;

public record PatchPatientInternalCommand(
        String idCardNo,
        LocalDate birthDay,
        String phoneNumber,
        PatchUserInternalCommand user
) {
}
