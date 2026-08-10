package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException() {
        super("Patient not found",HttpStatus.NOT_FOUND);
    }
}
