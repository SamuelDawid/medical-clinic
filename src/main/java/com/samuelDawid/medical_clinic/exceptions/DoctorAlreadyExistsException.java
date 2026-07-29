package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorAlreadyExistsException extends MedicalClinicException {
    public DoctorAlreadyExistsException() {
        super("Doctor already exists", HttpStatus.CONFLICT);
    }
}
