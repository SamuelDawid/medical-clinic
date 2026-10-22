package internalCommands.appointment;

import java.time.LocalDateTime;

public record CreateAppointmentInternalCommand(
        Long patientId,
        Long doctorId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
