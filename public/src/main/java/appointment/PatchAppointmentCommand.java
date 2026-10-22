package appointment;

import java.time.LocalDateTime;

public record PatchAppointmentCommand(
        Long appointmentId,
        LocalDateTime time
) {
}
