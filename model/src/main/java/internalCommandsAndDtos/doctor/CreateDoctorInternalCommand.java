package internalCommandsAndDtos.doctor;

import internalCommandsAndDtos.user.CreateUserInternalCommand;

import java.util.Set;

public record CreateDoctorInternalCommand(
        String medicalSpecialty,
        CreateUserInternalCommand user,
        Set<Long> institutionId
) {
}
