package doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import user.PatchUserCommand;

public record PatchDoctorCommand(
        @Schema(description = "Medical Speciality", example = "Anesthesiologists", requiredMode = Schema.RequiredMode.REQUIRED)
        String medicalSpecialty,
        PatchUserCommand user
) {
}
