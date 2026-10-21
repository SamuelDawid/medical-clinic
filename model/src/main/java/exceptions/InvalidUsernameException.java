package exceptions;


public class InvalidUsernameException extends MedicalClinicException {
    public InvalidUsernameException() {
        super("Invalid username", 400);
    }
}
