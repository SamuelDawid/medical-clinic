package exceptions;

public class AppointmentAlreadyTakenException extends MedicalClinicException {
    public AppointmentAlreadyTakenException() {
        super("This Appointment is already taken", 409);
    }
}
