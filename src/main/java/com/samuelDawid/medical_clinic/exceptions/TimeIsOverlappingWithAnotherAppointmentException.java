package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class TimeIsOverlappingWithAnotherAppointmentException extends MedicalClinicException {
    public TimeIsOverlappingWithAnotherAppointmentException() {
        super("Another appointent at this time already exists", HttpStatus.CONFLICT);
    }
}
