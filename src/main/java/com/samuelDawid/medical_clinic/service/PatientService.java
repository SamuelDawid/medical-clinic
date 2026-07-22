package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.patientMapper.PatientMapper;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository repository;
    private final EmailValidator emailValidator;
    private final PatientMapper mapperInstance;

    public List<PatientDto> findAll() {
        return repository.findAll().stream().map(mapperInstance::toPatientDto).toList();
    }

    public PatientDto findByEmail(@NonNull String email) {
        String normalized = EmailValidator.normalize(email);
        if (emailValidator.validate(normalized)) {
            throw new InvalidEmailException(email);
        }
        Patient patient = repository.findByEmail(normalized)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return mapperInstance.toPatientDto(patient);
    }

    public PatientDto addPatient(@NonNull CreatePatientCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.email());

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(patientCommand.email());
        }

        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(emailNormalizer);
        repository.create(patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        String emailNormalizer = EmailValidator.normalize(email);

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(email);
        }
        repository.delete(emailNormalizer);
    }

    public PatientDto updatePatient(@NonNull String email, @NonNull CreatePatientCommand patientCommand) {
        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(EmailValidator.normalize(email));
        if (!emailValidator.validate(patient.getEmail())) {
            throw new InvalidEmailException(patient.getEmail());
        }
        repository.update(email, patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        Patient patientToUpdate = repository.findByEmail(EmailValidator.normalize(email))
                .orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.setPassword(newPassword);
    }
}
