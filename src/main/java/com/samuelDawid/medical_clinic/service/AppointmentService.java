package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentsRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public PageDto<AppointmentDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(mapper::toDto));
    }

    public PageDto<AppointmentDto> findAllByPatientId(@NonNull Long id, Pageable pageable) {
         return PageDto.from(repository.findAllByPatientId(id, pageable)
                .map(mapper::toDto));
    }

    public AppointmentDto findById(@NonNull Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        return mapper.toDto(appointment);
    }

    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        Appointment appointment = mapper.toEntity(command);

        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(DoctorNotFoundException::new);
        appointment.setDoctor(doctor);

        validateDate(appointment.getDate(), appointment.getStartTime());
        validateTimeOfTheVisit(appointment);
        assignPatientAtAppointmentCreation(command, appointment);

        repository.save(appointment);
        return mapper.toDto(appointment);
    }

    public void removePatientFromVisit(@NonNull Long appointmentId) {
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        appointment.setPatient(null);
        repository.save(appointment);
    }

    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        Appointment appointment = repository.findById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if (appointment.getPatient() != null) {
            throw new AppointmentAlreadyTakenException();
        }

        validateDate(appointment.getDate(), appointment.getStartTime());
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

    private void validateDate(@NonNull LocalDate date, @NonNull LocalTime time) {
        LocalDateTime appointment = LocalDateTime.of(date, time);
        if (appointment.isBefore(LocalDateTime.now())) {
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void assignPatientAtAppointmentCreation(@NonNull CreateAppointmentCommand command, Appointment appointment) {
        if (command.patientId() != null) {
            Patient patient = patientRepository.findById(command.patientId())
                    .orElseThrow(PatientWithIdNotFoundException::new);
            appointment.setPatient(patient);
        }
    }

    private void validateTimeOfTheVisit(@NonNull Appointment appointmentToCreate) {
        int minutes = appointmentToCreate.getStartTime()
                .getMinute();
        if (!validateMinutes(minutes)) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        Set<Appointment> appointments = repository.findByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                appointmentToCreate.getDoctor(),
                appointmentToCreate.getDate(),
                appointmentToCreate.getEndTime(),
                appointmentToCreate.getStartTime()
        );
        if (!appointments.isEmpty()) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private boolean validateMinutes(int minutes) {
        return minutes % 15 == 0;
    }
}
