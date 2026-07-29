package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InstitutionAlreadyExistsException extends MedicalClinicException {
    public InstitutionAlreadyExistsException() {
        super("Institution Already Exists", HttpStatus.CONFLICT);
    }
}
