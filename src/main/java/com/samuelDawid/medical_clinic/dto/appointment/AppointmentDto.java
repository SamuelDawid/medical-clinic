package com.samuelDawid.medical_clinic.dto.appointment;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        LocalDateTime timeAndDate,
        String doctorName,
        String patientName
) {
}
