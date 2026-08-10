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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentsRepository repository;
    private final AppointmentMapper mapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(mapper::toDto));
    }
    @Transactional(readOnly = true)
    public PageDto<AppointmentDto> findAllByPatientId(@NonNull Long id, Pageable pageable) {
        return PageDto.from(repository.findAllByPatientId(id, pageable)
                .map(mapper::toDto));
    }
    @Transactional(readOnly = true)
    public AppointmentDto findById(@NonNull Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        return mapper.toDto(appointment);
    }
    @Transactional
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        Appointment appointment = mapper.toEntity(command);

        Doctor doctor = doctorRepository.findByIdForUpdate(command.doctorId())
                .orElseThrow(DoctorNotFoundException::new);
        appointment.setDoctor(doctor);

        validateDate(appointment.getStartDateTime());
        validateTimeOfTheVisit(appointment);
        assignPatientAtAppointmentCreation(command, appointment);

        return mapper.toDto(appointment);
    }
    @Transactional
    public void removePatientFromVisit(@NonNull Long appointmentId) {
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        appointment.setPatient(null);
    }
    @Transactional
    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        Appointment appointment = repository.findWithLockById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if (appointment.getPatient() != null) {
            throw new AppointmentAlreadyTakenException();
        }

        validateDate(appointment.getStartDateTime());
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(PatientWithIdNotFoundException::new);
        appointment.setPatient(patient);
        return mapper.toDto(appointment);
    }
    @Transactional
    public void delete(@NonNull Long appointmentId) {
        Appointment appointment = repository.findById(appointmentId)
                .orElseThrow(AppointmentDoesNotExistsException::new);
        repository.delete(appointment);
    }

    private void validateDate(@NonNull LocalDateTime dateAndTime) {
        if (dateAndTime.isBefore(LocalDateTime.now())) {
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
        int minutes = appointmentToCreate.getStartDateTime()
                .getMinute();
        if (!validateMinutes(minutes)) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        Set<Appointment> appointments = repository.findByDoctorAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                appointmentToCreate.getDoctor(),
                appointmentToCreate.getEndDateTime(),
                appointmentToCreate.getStartDateTime()
        );
        if (!appointments.isEmpty()) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private boolean validateMinutes(int minutes) {
        return minutes % 15 == 0;
    }
}
