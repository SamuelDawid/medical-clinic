package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PatientAlreadyExitsException extends RuntimeException {
    public PatientAlreadyExitsException(String email) {

        super("Patient with email " + email + " already exists");;
    }
}
