package exceptions;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor "+ id +" not found", 404);
    }
}
