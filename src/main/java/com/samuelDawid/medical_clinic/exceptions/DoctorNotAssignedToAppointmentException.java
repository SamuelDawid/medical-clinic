package com.samuelDawid.medical_clinic.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorNotAssignedToAppointmentException extends MedicalClinicException {
    public DoctorNotAssignedToAppointmentException() {
        super("Doctor Not Assigned To Appointment", HttpStatus.BAD_REQUEST);
    }
}
