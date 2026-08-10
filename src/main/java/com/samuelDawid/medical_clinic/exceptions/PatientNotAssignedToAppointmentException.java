package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientNotAssignedToAppointmentException extends MedicalClinicException {
    public PatientNotAssignedToAppointmentException() {
        super("Patient Not Assigned To Appointment", HttpStatus.BAD_REQUEST);
    }
}
