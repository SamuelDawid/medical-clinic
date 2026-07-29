package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException() {
        super("Doctor not found", HttpStatus.NOT_FOUND);
    }
}
