package com.samuelDawid.medical_clinic.dto.appointment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        @Schema(description = "Patient id", example = "1")
        Long patientId,
        @Schema(description = "Doctor id", example = "2")
        Long doctorId,
        LocalDateTime timeAndDate

) {
}
