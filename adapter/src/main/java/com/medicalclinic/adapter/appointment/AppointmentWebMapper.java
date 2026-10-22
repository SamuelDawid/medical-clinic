package com.medicalclinic.adapter.appointment;

import appointment.AppointmentDto;
import appointment.AssignPatientToAppointmentCommand;
import appointment.AvailableAppointmentSummary;
import appointment.CreateAppointmentCommand;
import internalCommandsAndDtos.appointment.AssignPatientToAppointmentInternalCommand;
import internalCommandsAndDtos.appointment.CreateAppointmentInternalCommand;
import internalCommandsAndDtos.critera.AppointmentSearchCriteria;
import internalCommandsAndDtos.critera.AvailableAppointmentCriteria;
import models.AppointmentPOJO;
import models.UserPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentWebMapper {
    @Mapping(target = "doctorName", source = "doctor.user")
    @Mapping(target = "patientName", source = "patient.user")
    AppointmentDto toDto(AppointmentPOJO appointment);

    @Mapping(target = "doctorName", source = "doctor.user")
    AvailableAppointmentSummary toAvailableSummary(AppointmentPOJO appointment);

    default String fullName(UserPOJO user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    CreateAppointmentInternalCommand toCommand(CreateAppointmentCommand command);

    AssignPatientToAppointmentInternalCommand toCommand(AssignPatientToAppointmentCommand command);

    AppointmentSearchCriteria toCriteria(AppointmentSearchParams params);

    AvailableAppointmentCriteria toCriteria(AvailableAppointmentParams params);
}
