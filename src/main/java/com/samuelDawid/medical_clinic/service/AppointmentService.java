package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.dto.appointment.PatchAppointmentCommand;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.mappers.AppointmetMapper;
import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.repository.AppointmentsRepository;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentsRepository repository;
    private final AppointmetMapper mapper;

    public Set<AppointmentDto> findAll(){
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toSet());
    }
    public AppointmentDto findById(@NonNull Long id){
        Appointment appointment = repository.findById(id)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        return mapper.toDto(appointment);
    }
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        Appointment appointment = mapper.toEntity(command);
        validateDate(appointment.getDateTime());
        validateTimeOfTheVisit(appointment);
        validateDoctorAndPatient(appointment);

        repository.save(appointment);
        return mapper.toDto(appointment);
    }
    public void delete(@NonNull Long appointmentId){
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        repository.delete(appointment);
    }
    private void validateDate(@NonNull LocalDateTime timeOfVisit) {
        LocalDate date = timeOfVisit.toLocalDate();
        if (date.isBefore(LocalDate.now())) {
            throw new InvalideDateOfAppointmentException();
        }
    }

    private void validateDoctorAndPatient(@NonNull Appointment appointment) {
        if (appointment.getDoctor() == null) {
            throw new DoctorNotAssignedToAppointmentException();
        }
        if (appointment.getPatient() == null) {
            throw new PatientNotAssignedToAppointmentException();
        }
    }

    private void validateTimeOfTheVisit(@NonNull Appointment appointmentToCreate) {
        int minutes = appointmentToCreate.getDateTime().getMinute();
        if (minutes != 15 || minutes != 30 || minutes != 45 || minutes != 00) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        List<Appointment> appointments = repository.findAll().stream()
                .filter(appointment -> appointment.getDateTime()
                        .getDayOfWeek().equals(appointmentToCreate.getDateTime().getDayOfWeek()))
                .toList();

        for (Appointment appointment : appointments){
            if(isOverlappingWithExistingApoointment(appointmentToCreate,appointment)){
                throw new InvalideDateOfAppointmentException();
            }
        }
    }

    private boolean isOverlappingWithExistingApoointment(@NonNull Appointment appointmentToCreate,@NonNull Appointment existingAppointment){
        LocalTime startOfAppointmentToCreate = appointmentToCreate.getDateTime().toLocalTime();
        LocalTime endOfAppointmentToCreate = appointmentToCreate.getDateTime()
                .plusMinutes(appointmentToCreate.getAppointmentLengthInMinutes()).toLocalTime();

        LocalTime startOfExistingAppointment = existingAppointment.getDateTime().toLocalTime();
        LocalTime endOfExistingAppointment = existingAppointment.getDateTime()
                .plusMinutes(existingAppointment.getAppointmentLengthInMinutes()).toLocalTime();

        if(startOfAppointmentToCreate.isBefore(endOfExistingAppointment) && startOfAppointmentToCreate.isAfter(startOfExistingAppointment)){
            return true;
        }
        return endOfAppointmentToCreate.isAfter(startOfExistingAppointment) && endOfAppointmentToCreate.isBefore(endOfExistingAppointment);
    }
}
