package internalCommandsAndDtos.patient;


import internalCommandsAndDtos.user.CreateUserInternalCommand;
import org.mapstruct.Mapping;

import java.time.LocalDate;

public record CreatePatientInternalCommand(
        String idCardNo,
        LocalDate birthDay,
        String phoneNumber,
        @Mapping(target = "user")
        CreateUserInternalCommand user
) {
}


