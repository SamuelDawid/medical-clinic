package com.medicalclinic.adapter.appointment;

import enums.Timeframe;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AppointmentSearchParams(
        @Schema(description = "Patient id", example = "1")
        Long patientId,
        @Schema(description = "Doctor id", example = "1")
        Long doctorId,
        @Schema(description = "Doctor medical specialty", example = "Cardiology")
        String specialization,
        @Schema(description = "Appointments starting from (inclusive)", example = "2026-11-10T09:00:00")
        LocalDateTime from,
        @Schema(description = "Appointments starting before (exclusive)", example = "2026-11-10T17:00:00")
        LocalDateTime to,
        @Schema(description = "PAST, FUTURE or ALL", example = "FUTURE")
        Timeframe timeframe
) {
}
