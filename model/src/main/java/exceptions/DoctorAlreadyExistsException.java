package exceptions;

public class DoctorAlreadyExistsException extends MedicalClinicException {
    public DoctorAlreadyExistsException() {
        super("Doctor already exists", 409);
    }
}
