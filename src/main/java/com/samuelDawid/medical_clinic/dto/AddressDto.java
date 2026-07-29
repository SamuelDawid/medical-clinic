package com.samuelDawid.medical_clinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressDto(
        @Schema(description = "City", example = "Chicago")
        String city,
        @Schema(description = "Postcode", example = "IG95RH")
        String postCode,
        @Schema(description = "street",example = "Highroad")
        String street,
        @Schema(description = "flat/building number", example = "18/3A")
        String buildingNumber
) {
}
