package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.AssignPatientToAppointmentCommand;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.mappers.AppointmentMapper;
import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.repository.AppointmentsRepository;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
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
    private final AppointmentMapper mapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public Set<AppointmentDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toSet());
    }

    public AppointmentDto findById(@NonNull Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        return mapper.toDto(appointment);
    }

    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        Appointment appointment = mapper.toEntity(command);
        validateDate(appointment.getTimeAndDate());
        validateTimeOfTheVisit(appointment);

        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(DoctorNotFoundException::new);
        appointment.setDoctor(doctor);
        if (command.patientId() != null) {
            Patient patient = patientRepository.findById(command.patientId())
                    .orElseThrow(PatientWithIdNotFoundException::new);
            appointment.setPatient(patient);
        }
        repository.save(appointment);
        return mapper.toDto(appointment);
    }

    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        Appointment appointment = repository.findById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if(appointment.getPatient() != null){ throw new AppointmentAlreadyTakenException();}
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(PatientWithIdNotFoundException::new);
        appointment.setPatient(patient);
        repository.save(appointment);
        return mapper.toDto(appointment);
    }

    public void delete(@NonNull Long appointmentId) {
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        repository.delete(appointment);
    }

    private void validateDate(@NonNull LocalDateTime timeOfVisit) {
        LocalDate date = timeOfVisit.toLocalDate();
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void validateTimeOfTheVisit(@NonNull Appointment appointmentToCreate) {
        int minutes = appointmentToCreate.getTimeAndDate().getMinute();
        if (!validateMinutes(minutes)) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        List<Appointment> appointments = repository.findAll().stream()
                .filter(appointment -> appointment.getTimeAndDate()
                        .getDayOfWeek().equals(appointmentToCreate.getTimeAndDate().getDayOfWeek()))
                .toList();

        for (Appointment appointment : appointments) {
            if (isOverlappingWithExistingAppointment(appointmentToCreate, appointment)) {
                throw new TimeIsOverlappingWithAnotherAppointmentException();
            }
        }
    }

    private boolean validateMinutes(int minutes) {
        Set<Integer> allowedMinutes = Set.of(0, 15, 30, 45);
        return allowedMinutes.contains(minutes);
    }

    private boolean isOverlappingWithExistingAppointment(@NonNull Appointment appointmentToCreate, @NonNull Appointment existingAppointment) {
        LocalTime startOfAppointmentToCreate = appointmentToCreate.getTimeAndDate().toLocalTime();
        LocalTime endOfAppointmentToCreate = appointmentToCreate.getTimeAndDate()
                .plusMinutes(appointmentToCreate.getAppointmentLengthInMinutes()).toLocalTime();

        LocalTime startOfExistingAppointment = existingAppointment.getTimeAndDate().toLocalTime();
        LocalTime endOfExistingAppointment = existingAppointment.getTimeAndDate()
                .plusMinutes(existingAppointment.getAppointmentLengthInMinutes()).toLocalTime();

        if (startOfAppointmentToCreate.isBefore(endOfExistingAppointment) && startOfAppointmentToCreate.isAfter(startOfExistingAppointment)) {
            return true;
        }
        return endOfAppointmentToCreate.isAfter(startOfExistingAppointment) && endOfAppointmentToCreate.isBefore(endOfExistingAppointment);
    }
}
