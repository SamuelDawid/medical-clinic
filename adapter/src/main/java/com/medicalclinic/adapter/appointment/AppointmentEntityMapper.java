package com.medicalclinic.adapter.appointment;

import com.medicalclinic.adapter.doctor.DoctorEntityMapper;
import com.medicalclinic.adapter.patient.PatientEntityMapper;
import models.AppointmentPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DoctorEntityMapper.class, PatientEntityMapper.class})
public interface AppointmentEntityMapper {

    AppointmentPOJO toDomain(AppointmentEntity entity);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    AppointmentEntity toEntity(AppointmentPOJO pojo);
}
