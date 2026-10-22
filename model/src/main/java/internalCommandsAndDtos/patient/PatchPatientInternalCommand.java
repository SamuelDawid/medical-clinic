package internalCommandsAndDtos.patient;

import internalCommandsAndDtos.user.PatchUserInternalCommand;
import org.mapstruct.Mapping;

import java.time.LocalDate;

public record PatchPatientInternalCommand(
        String idCardNo,
        LocalDate birthDay,
        String phoneNumber,
        @Mapping(target = "user")
        PatchUserInternalCommand user
) {
}
