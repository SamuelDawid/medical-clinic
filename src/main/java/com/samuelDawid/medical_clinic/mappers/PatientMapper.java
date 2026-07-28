package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
     Patient toEntity(CreatePatientCommand patientCommand);
     PatientDto toPatientDto(Patient patient);
}
