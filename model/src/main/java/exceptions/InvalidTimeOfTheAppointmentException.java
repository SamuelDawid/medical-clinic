package exceptions;

public class InvalidTimeOfTheAppointmentException extends MedicalClinicException {
    public InvalidTimeOfTheAppointmentException() {
        super("Invalid Time Of The Appointment", 400);
    }
}
