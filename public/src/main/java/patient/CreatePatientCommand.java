package patient;

import io.swagger.v3.oas.annotations.media.Schema;
import user.CreateUserCommand;

import java.time.LocalDate;

public record CreatePatientCommand(
        @Schema(description = "ID card number", example = "1005200004554",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String idCardNo,
        @Schema(description = "Birth date in ISO format (YYYY-MM-DD)", example = "2000-02-22",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate birthDay,
        @Schema(description = "Phone number", example = "+48 609567865",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber,
        CreateUserCommand user
) {
}


