package doctor;

import institution.InstitutionDto;
import user.UserDto;

import java.util.Set;

public record DoctorDto(
        Long id,
        String medicalSpecialty,
        Set<InstitutionDto> institutionDto,
        UserDto userDto
) {
}
