package com.samuelDawid.medical_clinic.patientMapper;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import org.springframework.stereotype.Component;


public class PatientMapper {

    private PatientMapper() {}

    public static Patient toEntity(CreatePatientCommand patientCommand){
        return new Patient(patientCommand.email(),
                patientCommand.password(),
                patientCommand.idCardNo(),
                patientCommand.firstName(),
                patientCommand.lastName(),
                patientCommand.birthDay(),
                patientCommand.phoneNumber());
    }
    public static PatientDto toPatientDto(Patient patient){
        return new PatientDto(patient.getEmail(),patient.getFirstName(),patient.getLastName(),patient.getBirthDay(),patient.getPhoneNumber());
    }
}
