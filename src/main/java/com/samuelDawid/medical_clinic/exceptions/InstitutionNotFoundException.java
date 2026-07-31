package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InstitutionNotFoundException extends MedicalClinicException {
    public InstitutionNotFoundException() {
        super("Insitution NotFound", HttpStatus.NOT_FOUND);
    }
}
