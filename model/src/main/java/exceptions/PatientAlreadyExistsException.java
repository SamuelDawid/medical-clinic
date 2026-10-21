package exceptions;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String email) {
        super("Patient with email " + email + " already exists", 409);
        ;
    }
}
