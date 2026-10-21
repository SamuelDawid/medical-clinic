package exceptions;

public class InvalidEmailException extends MedicalClinicException {
    public InvalidEmailException(String email) {
        super("Invalid email: " + email, 400 );
    }
}
