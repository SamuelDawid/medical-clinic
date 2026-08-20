package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.mappers.PatientMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.repository.UserRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository repository;
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final UserPatcher userPatcher;
    @Transactional(readOnly = true)
    public PageDto<PatientDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(patientMapper::toPatientDto));
    }
    @Transactional(readOnly = true)
    public PatientDto findById(@NotNull Long id) {
        Patient patientToFind = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        return patientMapper.toPatientDto(patientToFind);
    }
    @Transactional
    public PatientDto create(@NonNull CreatePatientCommand patientCommand) {
        log.info("Starting creation of the patient");
        String emailNormalizer = EmailValidator.normalize(patientCommand.user()
                .email());
        log.debug("Email normalized: {} -> {}",patientCommand.user().email(),emailNormalizer);
        validateEmail(emailNormalizer);

        Patient patient = patientBuilder(patientCommand, emailNormalizer);

        repository.save(patient);
        log.info("Created Patient with id {}",patient.getId());
        return patientMapper.toPatientDto(patient);
    }
    @Transactional
    public void deleteById(@NonNull Long id) {
        log.info("Delete patient started");
        Patient patientToDelete = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        repository.delete(patientToDelete);
        log.info("Successfully deleted patient with id {}",id);
    }

    @Transactional
    public PatientDto updatePatient(@NonNull Long id, @NonNull PatchPatientCommand patientCommand) {
        log.info("Updating patient started");
        Patient patient = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        patient.update(patientCommand, userPatcher);
        log.info("Patient updated successfully");
        return patientMapper.toPatientDto(patient);
    }
    @Transactional
    public void updatePassword(@NonNull String newPassword, @NonNull Long id) {
        log.info("Updating patient password");
        if (newPassword.isBlank()) {
            throw new InvalidPasswordException();
        }
        Patient patientToUpdate = repository.findById(id)
                .orElseThrow(PatientNotFoundException::new);
        patientToUpdate.getUser()
                .setPassword(newPassword);
        log.info("Password updated");
    }

    private void validateEmail(String email) {
        if(!emailValidator.validate(email)){
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            throw new PatientAlreadyExistsException(email);
        }
    }

    private Patient patientBuilder(@NonNull CreatePatientCommand patientCommand, String email) {
        Patient patient = patientMapper.toEntity(patientCommand);
        User user = userMapper.toEntity(patientCommand.user());
        user.setEmail(email);
        patient.setUser(user);
        return patient;
    }
}
