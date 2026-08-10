package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidPasswordException;
import com.samuelDawid.medical_clinic.exceptions.PatientAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.PatientWithIdNotFoundException;
import com.samuelDawid.medical_clinic.mappers.PatientMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository repository;
    private final EmailValidator emailValidator;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final UserPatcher userPatcher;

    public PageDto<PatientDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(patientMapper::toPatientDto));
    }

    public PatientDto findById(@NotNull Long id) {
        Patient patientToFind = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        return patientMapper.toPatientDto(patientToFind);
    }

    public PatientDto addPatient(@NonNull CreatePatientCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.user()
                .email());
        emailValidator.validate(emailNormalizer);
        validateEmailIsFree(emailNormalizer);

        Patient patient = patientBuilder(patientCommand, emailNormalizer);

        repository.save(patient);
        return patientMapper.toPatientDto(patient);
    }

    public void deleteById(@NonNull Long id) {
        Patient patientToDelete = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        repository.delete(patientToDelete);
    }

    @Transactional
    public PatientDto updatePatient(@NonNull Long id, @NonNull PatchPatientCommand patientCommand) {
        Patient patient = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        patient.update(patientCommand, userPatcher);
        return patientMapper.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull Long id) {
        if (newPassword.isBlank()) {
            throw new InvalidPasswordException();
        }
        Patient patientToUpdate = repository.findById(id)
                .orElseThrow(PatientNotFoundException::new);
        patientToUpdate.getUser()
                .setPassword(newPassword);
        repository.save(patientToUpdate);
    }

    private void validateEmailIsFree(String email) {
        if (repository.findByUserEmail(email)
                .isPresent()) {
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
