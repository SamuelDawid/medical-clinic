package internalCommandsAndDtos.critera;

import java.time.LocalDateTime;

public record AvailableAppointmentCriteria(
        Long doctorId,
        String specialization,
        LocalDateTime from,
        LocalDateTime to
) {
}
