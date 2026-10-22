package patient;

import exceptions.InvalidEmailException;
import exceptions.InvalidPasswordException;
import exceptions.PatientAlreadyExistsException;
import exceptions.PatientWithIdNotFoundException;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.patient.CreatePatientInternalCommand;
import internalCommandsAndDtos.patient.PatchPatientInternalCommand;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import models.PatientPOJO;
import user.UserRepositoryPort;
import validators.EmailValidator;

@Slf4j
@AllArgsConstructor
public class PatientServiceImp implements PatientService{
    private final PatientRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final EmailValidator emailValidator;

    public PageResult<PatientPOJO> findAll(PageQuery pageQuery) {
        return repository.findAll(pageQuery);
    }

    public PatientPOJO findById(@NonNull Long id) {
        return getPatientOrThrow(id);
    }

    public PatientPOJO create(@NonNull CreatePatientInternalCommand patientCommand) {
        log.info("Starting creation of the patient");
        String emailNormalizer = EmailValidator.normalize(patientCommand.user().email());
        log.debug("Email normalized: {} -> {}", patientCommand.user().email(), emailNormalizer);
        validateEmail(emailNormalizer);

        PatientPOJO patient = PatientPOJO.create(patientCommand);

        PatientPOJO saved = repository.save(patient);
        log.info("Created Patient with id {}", saved.getId());
        return saved;
    }

    public void deleteById(@NonNull Long id) {
        log.info("Delete patient started");
        getPatientOrThrow(id);
        repository.delete(id);
        log.info("Successfully deleted patient with id {}", id);
    }

    public PatientPOJO updatePatient(@NonNull Long id, @NonNull PatchPatientInternalCommand patientCommand) {
        log.info("Updating patient started");
        PatientPOJO patient =  getPatientOrThrow(id);
        patient.update(patientCommand);
        log.info("Patient updated successfully");
        return patient;
    }

    public void updatePassword(@NonNull String newPassword, @NonNull Long id) {
        log.info("Updating patient password");
        if (newPassword.isBlank()) {
            log.warn("blank or null password");
            throw new InvalidPasswordException();
        }
        PatientPOJO patientToUpdate = getPatientOrThrow(id);
        patientToUpdate.getUser()
                .setPassword(newPassword);
        log.info("Password updated");
    }

    private void validateEmail(String email) {
        if (!emailValidator.validate(email)) {
            log.warn("email : {} not a valid email",email);
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("user with email {} already exists",email);
            throw new PatientAlreadyExistsException(email);
        }
    }

    private PatientPOJO getPatientOrThrow(Long id){
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Updating patient with id {} failed, not found",id);
                    return new PatientWithIdNotFoundException();
                });
    }
}
