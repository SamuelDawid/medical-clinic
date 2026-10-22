package com.medicalclinic.adapter.patient;

import internalCommandsAndDtos.patient.CreatePatientInternalCommand;
import internalCommandsAndDtos.patient.PatchPatientInternalCommand;
import models.PatientPOJO;
import org.mapstruct.Mapper;
import patient.CreatePatientCommand;
import patient.PatchPatientCommand;
import patient.PatientDto;

@Mapper(componentModel = "spring")
public interface PatientWebMapper {
    PatientDto toDto(PatientPOJO pojo);
    CreatePatientInternalCommand toCreateCommand(CreatePatientCommand command);
    PatchPatientInternalCommand toPatchCommand(PatchPatientCommand command);
}
