package exceptions;


public class InvalidPasswordException extends MedicalClinicException {
    public InvalidPasswordException() {
        super("Password does not meet criteria", 400);
    }
}
