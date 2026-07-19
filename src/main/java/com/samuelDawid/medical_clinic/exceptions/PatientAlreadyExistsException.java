package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PatientAlreadyExistsException extends RuntimeException {
    public PatientAlreadyExistsException(String email) {

        super("Patient with email " + email + " already exists");;
    }
}
