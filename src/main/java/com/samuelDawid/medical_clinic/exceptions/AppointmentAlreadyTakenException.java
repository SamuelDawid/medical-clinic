package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyTakenException extends MedicalClinicException {
    public AppointmentAlreadyTakenException() {
        super("This Appointment is already taken", HttpStatus.CONFLICT);
    }
}
