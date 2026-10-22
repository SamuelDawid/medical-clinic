package internalCommands.institution;

import models.DoctorPOJO;
import models.institution.InstitutionPOJO;

import java.util.Set;

public record InstitutionDoctorsInternalDto(
        InstitutionPOJO institution,
        Set<DoctorPOJO> doctorsDto
) {
}
