package internalCommands.appointment;

public record AssignPatientToAppointmentInternalCommand(
        Long patientId,
        Long appointmentId
) {
}
