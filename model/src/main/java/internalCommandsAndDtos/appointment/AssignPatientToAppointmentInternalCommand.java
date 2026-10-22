package internalCommandsAndDtos.appointment;

public record AssignPatientToAppointmentInternalCommand(
        Long patientId,
        Long appointmentId
) {
}
