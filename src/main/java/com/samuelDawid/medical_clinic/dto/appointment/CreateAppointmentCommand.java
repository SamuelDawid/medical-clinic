package com.samuelDawid.medical_clinic.dto.appointment;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        Long patientId,
        Long doctorId,
        LocalDateTime timeAndDate

) {
}
