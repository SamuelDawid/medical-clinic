package internalCommands.institution;

import internalCommands.doctor.DoctorSummaryInternalDto;

import java.util.Set;

public record InstitutionDoctorsInternalDto(
        String institutionName,
        Set<DoctorSummaryInternalDto> doctorsDto
) {
}
