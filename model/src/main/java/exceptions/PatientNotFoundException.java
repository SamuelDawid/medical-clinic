package exceptions;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException() {
        super("Patient not found",404);
    }
}
