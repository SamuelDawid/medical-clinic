package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
     Patient toEntity(CreatePatientCommand patientCommand);
     @Mapping(target = "userDto", source = "user")
     PatientDto toPatientDto(Patient patient);
}
