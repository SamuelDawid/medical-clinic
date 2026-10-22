package patient;

import io.swagger.v3.oas.annotations.media.Schema;
import user.UserDto;

import java.time.LocalDate;


public record PatientDto(
        Long id,
        UserDto userDto,
        @Schema(description = "Birth date in ISO format (YYYY-MM-DD)", example = "2000-02-22")
        LocalDate birthDay,
        @Schema(description = "Phone number", example = "+48 609567865")
        String phoneNumber
) {}
