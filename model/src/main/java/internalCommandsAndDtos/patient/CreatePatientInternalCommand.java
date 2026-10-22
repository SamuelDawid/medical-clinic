package internalCommandsAndDtos.patient;


import internalCommandsAndDtos.user.CreateUserInternalCommand;

import java.time.LocalDate;

public record CreatePatientInternalCommand(
        String idCardNo,
        LocalDate birthDay,
        String phoneNumber,
        CreateUserInternalCommand user
) {
}


