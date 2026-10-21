package exceptions;

public class DoctorNotAssignedToAppointmentException extends MedicalClinicException {
    public DoctorNotAssignedToAppointmentException() {
        super("Doctor Not Assigned To Appointment", 400);
    }
}
