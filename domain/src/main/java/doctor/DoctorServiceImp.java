package doctor;

import affiliationService.AffiliationService;

import exceptions.DoctorAlreadyExistsException;
import exceptions.DoctorNotFoundException;
import exceptions.InvalidEmailException;
import internalCommands.PageQuery;
import internalCommands.PageResult;
import internalCommands.doctor.CreateDoctorInternalCommand;
import internalCommands.doctor.DoctorSummaryInternalDto;
import internalCommands.doctor.PatchDoctorInternalCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.DoctorPOJO;
import user.UserRepositoryPort;
import validators.EmailValidator;


@Slf4j
@RequiredArgsConstructor
public class DoctorServiceImp implements DoctorService{
    private final DoctorRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final AffiliationService affiliationService;
    private final EmailValidator emailValidator;

    public PageResult<DoctorPOJO> findAll( PageQuery pageQuery) {
        return repository.findAll(pageQuery);
    }

    public DoctorPOJO findById(@NonNull Long id) {
        return (getDoctorOrThrow(id));
    }

    public PageResult<DoctorPOJO> findAll(String speciality, PageQuery pageQuery) {
        log.info("Searching doctors with speciality {}", speciality);
        return (speciality == null || speciality.isBlank())
                ? repository.findAll(pageQuery)
                : repository.findByMedicalSpecialtyIgnoreCase(speciality, pageQuery);
    }

    public DoctorPOJO create(@NonNull CreateDoctorInternalCommand command) {
        log.info("Creating new Doctor");
        String email = EmailValidator.normalize(command.user().email());
        validateEmail(email);

        DoctorPOJO doctor = DoctorPOJO.create(command);
        affiliationService.assignInstitutionsByIdToDoctor(command.institutionId(), doctor);

        DoctorPOJO saved = repository.save(doctor);
        log.info("Doctor {} created successfully", saved.getId());
        return saved;
    }

    public DoctorPOJO update(@NonNull Long id, @NonNull PatchDoctorInternalCommand command) {
        log.info("Updating Doctor {}", id);
        DoctorPOJO doctor = getDoctorOrThrow(id);
        doctor.update(command);
        log.info("Doctor {} updated successfully", id);
        return doctor;
    }

    public void delete(@NonNull Long id) {
        log.info("Removing Doctor {}", id);
        getDoctorOrThrow(id);
        repository.delete(id);
        log.info("Doctor {} removed successfully", id);
    }

    private void validateEmail(String email) {
        if (!emailValidator.validate(email)) {
            log.warn("email : {} not a valid email", email);
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("user with email {} already exists", email);
            throw new DoctorAlreadyExistsException();
        }
    }

    private DoctorPOJO getDoctorOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor with id {} does not exists", id);
                    return new DoctorNotFoundException(id);
                });
    }
}
