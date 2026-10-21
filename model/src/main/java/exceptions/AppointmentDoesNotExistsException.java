package exceptions;

public class AppointmentDoesNotExistsException extends MedicalClinicException {
    public AppointmentDoesNotExistsException() {
        super("Appointment Does Not Exists", 404);
    }
}
