package com.medicalclinic.adapter.doctor;

import models.DoctorPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorEntityMapper {
    DoctorPOJO toDomain(DoctorEntity doctorEntity);
    DoctorEntity toEntity(DoctorPOJO pojo);
}
