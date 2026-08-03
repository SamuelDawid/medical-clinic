package com.samuelDawid.medical_clinic.dto.appointment;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssignPatientToAppointmentCommand(
        @Schema(description = "Patient id", example = "1")
        Long patientId,
        @Schema(description = "Appointment id", example = "1")
        Long appointmentId
) {
}
