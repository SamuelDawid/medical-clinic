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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
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
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional
    public AppointmentDto create(@NonNull CreateAppointmentCommand command) {
        log.info("Creating appointment with doctor Id {} and patient Id {} with date {} to {}",
                command.doctorId(), command.patientId(), command.startDateTime(), command.endDateTime());
        Appointment appointment = mapper.toEntity(command);

        Doctor doctor = doctorRepository.findByIdForUpdate(command.doctorId())
                .orElseThrow(() -> {
                    log.warn("doctor with id {} does not exist", command.doctorId());
                    return new DoctorNotFoundException(command.doctorId());
                });
        appointment.setDoctor(doctor);

        validateDate(appointment.getStartDateTime());
        validateTimeOfTheVisit(appointment);
        assignPatientAtAppointmentCreation(command, appointment);
        Appointment saved = repository.save(appointment);
        log.info("Appointment with Id {} Created", appointment.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    public void removePatientFromVisit(@NonNull Long appointmentId) {
        log.info("Removing Patient from Visit with Id {}", appointmentId);
        Appointment appointment = findOrThrow(appointmentId);
        appointment.setPatient(null);
        log.info("Patient removed successfully from visit with Id {} ", appointmentId);
    }

    @Transactional
    public AppointmentDto assignPatientToAppointment(@NonNull AssignPatientToAppointmentCommand command) {
        log.info("Assigning Patient with Id {} To Appointment with Id {}", command.patientId(), command.appointmentId());
        Appointment appointment = repository.findWithLockById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if (appointment.getPatient() != null) {
            log.warn("Couldn't assign patient because appointment already exists");
            throw new AppointmentAlreadyTakenException();
        }

        validateDate(appointment.getStartDateTime());
        Patient patient = patientRepository.findById(command.patientId())
                .orElseThrow(() ->{
                    log.warn("Could not assign patient {} to appointment {} as patient does not exists",command.patientId(),command.appointmentId());
                    return new PatientWithIdNotFoundException();
                });
        appointment.setPatient(patient);
        log.info("Patient with Id {} assigned successfully to visit with Id {}", command.patientId(), command.appointmentId());
        return mapper.toDto(appointment);
    }

    @Transactional
    public void delete(@NonNull Long appointmentId) {
        log.info("Deleting appointment with id {}", appointmentId);
        Appointment appointment = findOrThrow(appointmentId);
        repository.delete(appointment);
        log.info("Appointment {} deleted successfully", appointmentId);
    }

    private void validateDate(@NonNull LocalDateTime dateAndTime) {
        log.debug("Checking if time is correct {}", dateAndTime);
        if (dateAndTime.isBefore(LocalDateTime.now())) {
            log.warn("Invalid date, must be ahead of {}",LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void assignPatientAtAppointmentCreation(@NonNull CreateAppointmentCommand command, Appointment appointment) {
        if (command.patientId() != null) {
            Patient patient = patientRepository.findById(command.patientId())
                    .orElseThrow(() -> {
                        log.warn("Patient with id {} does not exist", command.patientId());
                        return new PatientNotFoundException();
                    });
            appointment.setPatient(patient);
        }
    }

    private void validateTimeOfTheVisit(@NonNull Appointment appointmentToCreate) {
        log.debug("Checking overlaps for doctor {} between {} and {}",
                appointmentToCreate.getDoctor().getId(), appointmentToCreate.getStartDateTime(), appointmentToCreate.getEndDateTime());
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
        log.debug("Found {} overlapping appointments", appointments.size());
        if (!appointments.isEmpty()) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private Appointment findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment with id {} does not exists", id);
                    return new AppointmentDoesNotExistsException();
                });
    }

    private boolean validateMinutes(int minutes) {
        return minutes % 15 == 0;
    }
}
