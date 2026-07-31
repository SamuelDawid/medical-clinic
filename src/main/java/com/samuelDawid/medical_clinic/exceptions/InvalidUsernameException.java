package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidUsernameException extends MedicalClinicException {
    public InvalidUsernameException() {
        super("Invalid username", HttpStatus.BAD_REQUEST);
    }
}
