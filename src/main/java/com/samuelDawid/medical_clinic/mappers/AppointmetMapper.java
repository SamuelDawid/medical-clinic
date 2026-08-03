package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmetMapper {
    Appointment toEntity(CreateAppointmentCommand command);
    AppointmentDto toDto(Appointment appointment);
}
