package doctor;

import user.UserDto;

import java.util.Set;

public record DoctorDto(
        Long id,
        String medicalSpecialty,
        Set<Long> institutionDto,
        UserDto userDto
) {
}
