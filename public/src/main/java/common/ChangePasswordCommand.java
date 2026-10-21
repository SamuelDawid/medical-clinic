package common;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePasswordCommand(
        @Schema(description = "Account newPassword", example = "newPassword123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String newPassword) {

}
