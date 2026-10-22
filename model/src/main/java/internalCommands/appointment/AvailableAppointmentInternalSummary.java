package internalCommands.appointment;

import java.time.LocalDateTime;

public record AvailableAppointmentInternalSummary(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String doctorName
) {
}
