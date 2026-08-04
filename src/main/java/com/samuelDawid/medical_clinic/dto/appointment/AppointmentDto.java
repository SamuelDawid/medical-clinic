package com.samuelDawid.medical_clinic.dto.appointment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDto(
        @Schema(description = "Appointment id", example = "1")
        Long id,
        @Schema(description = "Date of the appointment", example = "2026-08-10")
        LocalDate date,
        @Schema(description = "Start time of the appointment, always a full quarter of an hour", example = "10:15:00")
        LocalTime startTime,
        @Schema(description = "end time of the appointment", example = "10:25:00")
        LocalTime endTime,
        @Schema(description = "Full name of the doctor conducting the appointment", example = "John Doe")
        String doctorName,
        @Schema(description = "Full name of the assigned patient, null when the appointment is still free", example = "Jane Roe")
        String patientName
) {
}
