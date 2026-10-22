package institution;

import doctor.DoctorSummaryDto;

import java.util.Set;

public record InstitutionDoctorsDto(
        String institutionName,
        Set<DoctorSummaryDto> doctorsDto
) {
}
