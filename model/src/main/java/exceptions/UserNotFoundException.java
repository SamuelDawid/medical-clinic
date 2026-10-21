package exceptions;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException() {
        super("User not found", 404);
    }
}
