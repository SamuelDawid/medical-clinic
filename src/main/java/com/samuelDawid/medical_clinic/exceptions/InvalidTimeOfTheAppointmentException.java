package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTimeOfTheAppointmentException extends MedicalClinicException {
    public InvalidTimeOfTheAppointmentException() {
        super("Invalid Time Of The Appointment", HttpStatus.BAD_REQUEST);
    }
}
