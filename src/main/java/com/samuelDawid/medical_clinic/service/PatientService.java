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
    PatientMapper patientMapper;

    public List<PatientDto> all() {
        return repository.findAll().stream().map((a) -> patientMapper.toPatientDto(a)).toList();
    }

    public PatientDto findByEmail(@NonNull String email) {
        EmailValidator.normalize(email);
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.toPatientDto(patient);
    }

    public PatientDto addNewPatient(@NonNull CreatePatientCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.email());

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(patientCommand.email());
        }

        Patient patient = patientMapper.toEntity(patientCommand);
        patient.setEmail(emailNormalizer);
        repository.create(patient);
        return patientMapper.toPatientDto(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        EmailValidator.normalize(email);
        repository.delete(email);
    }

    public PatientDto updatePatient(@NonNull String email, @NonNull CreatePatientCommand patientCommand) {
        Patient patient = patientMapper.toEntity(patientCommand);
        patient.setEmail(EmailValidator.normalize(email));
        if (!emailValidator.validate(patient.getEmail())) {
            throw new InvalidEmailException(patient.getEmail());
        }
        repository.update(email, patient);
        return patientMapper.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        Patient patientToUpdate = repository.findByEmail(EmailValidator.normalize(email)).orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.setPassword(newPassword);
    }
}
