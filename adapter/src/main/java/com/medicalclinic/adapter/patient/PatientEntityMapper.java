package com.medicalclinic.adapter.patient;

import models.PatientPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientEntityMapper {
    PatientPOJO toDomain(PatientEntity patientEntity);

    PatientEntity toEntity(PatientPOJO patientPOJO);
}
