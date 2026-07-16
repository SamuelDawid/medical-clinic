package com.samuelDawid.medical_clinic.patientMapper;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
     Patient toEntity(CreatePatientCommand patientCommand);
     PatientDto toPatientDto(Patient patient);
}
