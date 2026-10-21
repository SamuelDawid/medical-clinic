package exceptions;

public class PatientWithIdNotFoundException extends MedicalClinicException {
    public PatientWithIdNotFoundException() {
        super("Patient with provided id not found", 404);
    }
}
