package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.AvailableAppointmentSummary;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "patient",ignore = true)
    Appointment toEntity(CreateAppointmentCommand command);
    @Mapping(target = "doctorName", source = "doctor.user")
    @Mapping(target = "patientName", source = "patient.user")
    AppointmentDto toDto(Appointment appointment);
    default String fullName(User user){
        if(user == null){ return  null;}
        return user.getFirstName() + " " + user.getLastName();
    }
    AvailableAppointmentSummary toAvailableAppointment(Appointment appointment);
}
