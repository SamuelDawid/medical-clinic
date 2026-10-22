package internalCommandsAndDtos.appointment;

import java.time.LocalDateTime;

public record PatchAppointmentInternalCommand(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
