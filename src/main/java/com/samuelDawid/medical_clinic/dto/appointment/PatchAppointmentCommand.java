package com.samuelDawid.medical_clinic.dto.appointment;

import java.time.LocalDateTime;

public record PatchAppointmentCommand(
        Long appointmentId,
        LocalDateTime time
) {
}
