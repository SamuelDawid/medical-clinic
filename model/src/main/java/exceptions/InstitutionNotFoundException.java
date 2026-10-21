package exceptions;

public class InstitutionNotFoundException extends MedicalClinicException {
    public InstitutionNotFoundException(Long id) {
        super("Institution "+id+" NotFound", 404);
    }
}
