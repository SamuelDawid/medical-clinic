package exceptions;

public class InstitutionAlreadyExistsException extends MedicalClinicException {
    public InstitutionAlreadyExistsException() {
        super("Institution Already Exists", 409);
    }
}
