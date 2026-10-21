package exceptions;

public class InvalidDateOfAppointmentException extends MedicalClinicException {
    public InvalidDateOfAppointmentException() {
        super("Date must be in the future", 400);
    }
}
