package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDateOfAppointmentException extends MedicalClinicException {
    public InvalidDateOfAppointmentException() {
        super("Date must be in the future", HttpStatus.BAD_REQUEST);
    }
}
