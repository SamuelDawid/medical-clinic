package internalCommandsAndDtos.critera;

import enums.Timeframe;

import java.time.LocalDateTime;

public record AppointmentSearchCriteria(
        Long patientId,
        Long doctorId,
        String specialization,
        LocalDateTime from,
        LocalDateTime to,
        Timeframe timeframe
) {
}
