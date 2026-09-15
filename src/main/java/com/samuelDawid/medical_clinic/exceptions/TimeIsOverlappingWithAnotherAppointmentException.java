package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class TimeIsOverlappingWithAnotherAppointmentException extends MedicalClinicException {
    public TimeIsOverlappingWithAnotherAppointmentException() {
        super("Another appointment at this time already exists", HttpStatus.CONFLICT);
    }
}
