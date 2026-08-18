package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InstitutionNotFoundException extends MedicalClinicException {
    public InstitutionNotFoundException(Long id) {
        super("Institution "+id+" NotFound", HttpStatus.NOT_FOUND);
    }
}
