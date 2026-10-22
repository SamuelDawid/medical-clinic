package appointment;

import doctor.DoctorRepositoryPort;
import exceptions.*;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.appointment.AssignPatientToAppointmentInternalCommand;
import internalCommandsAndDtos.appointment.CreateAppointmentInternalCommand;
import internalCommandsAndDtos.critera.AppointmentSearchCriteria;
import internalCommandsAndDtos.critera.AvailableAppointmentCriteria;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.AppointmentPOJO;
import models.DoctorPOJO;
import models.PatientPOJO;
import patient.PatientRepositoryPort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImp implements AppointmentService {
    private final AppointmentRepositoryPort repository;
    private final DoctorRepositoryPort doctorRepository;
    private final PatientRepositoryPort patientRepository;

    public PageResult<AppointmentPOJO> findAll(PageQuery pageQuery) {
        return repository.findAll(pageQuery);
    }

    public PageResult<AppointmentPOJO> search(AppointmentSearchCriteria criteria, PageQuery pageQuery) {
        return repository.search(criteria, pageQuery);
    }

    public PageResult<AppointmentPOJO> searchAvailable(AvailableAppointmentCriteria criteria, PageQuery pageQuery) {
        return repository.searchAvailable(criteria, pageQuery);
    }

    public PageResult<AppointmentPOJO> findAllByPatientId(@NonNull Long id, PageQuery pageQuery) {
        return repository.findAllByPatientId(id, pageQuery);
    }

    public AppointmentPOJO findById(@NonNull Long id) {
        return findOrThrow(id);
    }

    public AppointmentPOJO create(@NonNull CreateAppointmentInternalCommand command) {
        log.info("Creating appointment with doctor Id {} and patient Id {} with date {} to {}",
                command.doctorId(), command.patientId(), command.startDateTime(), command.endDateTime());
        DoctorPOJO doctor = doctorRepository.findByIdForUpdate(command.doctorId())
                .orElseThrow(() -> {
                    log.warn("doctor with id {} does not exist", command.doctorId());
                    return new DoctorNotFoundException(command.doctorId());
                });
        AppointmentPOJO appointment = AppointmentPOJO.create(command, null, doctor);   // doktor od razu, bez setDoctor
        validateDate(appointment.getStartDateTime());
        validateTimeOfTheVisit(appointment);
        assignPatientAtAppointmentCreation(command, appointment);
        AppointmentPOJO saved = repository.save(appointment);
        log.info("Appointment with Id {} Created", saved.getId());                     // saved, nie appointment
        return saved;
    }

    public void removePatientFromVisit(@NonNull Long appointmentId) {
        log.info("Removing Patient from Visit with Id {}", appointmentId);
        AppointmentPOJO appointment = findOrThrow(appointmentId);
        appointment.setPatient(null);
        repository.save(appointment);                                                  // BRAKOWAŁO – bez tego nic się nie zapisze
        log.info("Patient removed successfully from visit with Id {} ", appointmentId);
    }

    public AppointmentPOJO assignPatientToAppointment(@NonNull AssignPatientToAppointmentInternalCommand command) {
        log.info("Assigning Patient with Id {} To Appointment with Id {}", command.patientId(), command.appointmentId());
        AppointmentPOJO appointment = repository.findWithLockById(command.appointmentId())
                .orElseThrow(AppointmentDoesNotExistsException::new);
        if (appointment.getPatient() != null) {
            log.warn("Couldn't assign patient because appointment already exists");
            throw new AppointmentAlreadyTakenException();
        }

        validateDate(appointment.getStartDateTime());
        PatientPOJO patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> {
                    log.warn("Could not assign patient {} to appointment {} as patient does not exists", command.patientId(), command.appointmentId());
                    return new PatientWithIdNotFoundException();
                });
        appointment.setPatient(patient);
        AppointmentPOJO saved = repository.save(appointment);
        log.info("Patient with Id {} assigned successfully to visit with Id {}", command.patientId(), command.appointmentId());
        return saved;
    }

    public void delete(@NonNull Long appointmentId) {
        log.info("Deleting appointment with id {}", appointmentId);
        findOrThrow(appointmentId);
        repository.deleteById(appointmentId);
        log.info("Appointment {} deleted successfully", appointmentId);
    }

    private void validateDate(@NonNull LocalDateTime dateAndTime) {
        log.debug("Checking if time is correct {}", dateAndTime);
        if (dateAndTime.isBefore(LocalDateTime.now())) {
            log.warn("Invalid date, must be ahead of {}", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            throw new InvalidDateOfAppointmentException();
        }
    }

    private void assignPatientAtAppointmentCreation(@NonNull CreateAppointmentInternalCommand command, AppointmentPOJO appointment) {
        if (command.patientId() != null) {
            PatientPOJO patient = patientRepository.findById(command.patientId())
                    .orElseThrow(() -> {
                        log.warn("Patient with id {} does not exist", command.patientId());
                        return new PatientNotFoundException();
                    });
            appointment.setPatient(patient);
        }
    }

    private void validateTimeOfTheVisit(@NonNull AppointmentPOJO appointment) {
        if (!validateMinutes(appointment.getStartDateTime().getMinute())) {
            throw new InvalidTimeOfTheAppointmentException();
        }
        if (repository.existsOverlapping(appointment.getDoctor().getId(),
                appointment.getStartDateTime(), appointment.getEndDateTime())) {
            throw new TimeIsOverlappingWithAnotherAppointmentException();
        }
    }

    private AppointmentPOJO findOrThrow(Long id) {
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
