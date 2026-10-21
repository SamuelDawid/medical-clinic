package exceptions;

public class PatientNotAssignedToAppointmentException extends MedicalClinicException {
    public PatientNotAssignedToAppointmentException() {
        super("Patient Not Assigned To Appointment", 400);
    }
}
