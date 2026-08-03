package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalideDateOfAppointmentException extends MedicalClinicException {
    public InvalideDateOfAppointmentException() {
        super("Date must be in the future", HttpStatus.BAD_REQUEST);
    }
}
