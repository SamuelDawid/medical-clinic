package com.samuelDawid.medical_clinic.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super("Invalid email: " + email );
    }
}
