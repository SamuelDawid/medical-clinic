package com.samuelDawid.medical_clinic.dto;

public record ErrorMessageDto(String message,
                              int status,
                              String timeOfError
) {
}
