package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentDoesNotExistsException extends MedicalClinicException {
    public AppointmentDoesNotExistsException() {
        super("Appointment Does Not Exists", HttpStatus.NOT_FOUND);
    }
}
