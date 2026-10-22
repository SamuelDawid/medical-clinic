package com.medicalclinic.adapter.patient;

import internalCommands.patient.CreatePatientInternalCommand;
import internalCommands.patient.PatchPatientInternalCommand;
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
