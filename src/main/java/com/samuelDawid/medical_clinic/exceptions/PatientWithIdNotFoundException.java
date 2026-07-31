package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientWithIdNotFoundException extends MedicalClinicException {
    public PatientWithIdNotFoundException() {
        super("Patient with provided id not found", HttpStatus.NOT_FOUND);
    }
}
