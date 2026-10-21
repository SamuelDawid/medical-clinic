package exceptions;

public class UserAlreadyExistsException extends MedicalClinicException {
    public UserAlreadyExistsException() {
        super("User already exists", 409);
    }
}
