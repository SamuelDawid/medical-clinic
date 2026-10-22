package institution;


import common.CreateAddressCommand;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateInstitutionCommand(
        @Schema(description = "Name", example = "NewCare Clinic", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        CreateAddressCommand address
) {
}
