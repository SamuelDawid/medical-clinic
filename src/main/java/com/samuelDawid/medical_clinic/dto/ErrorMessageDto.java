package com.samuelDawid.medical_clinic.dto;

import java.time.LocalDate;

public record ErrorMessageDto(String message,
                              int status,
                              LocalDate timeOfError
) {
}
