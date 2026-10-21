package exceptions;

public class TimeIsOverlappingWithAnotherAppointmentException extends MedicalClinicException {
    public TimeIsOverlappingWithAnotherAppointmentException() {
        super("Another appointment at this time already exists", 409);
    }
}
