package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.exceptions.InvalidPasswordException;
import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.PatientWithIdNotFoundException;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.patientMapper.PatientMapper;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import jakarta.validation.constraints.NotNull;
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
        if (!emailValidator.validate(email)) {
            throw new InvalidEmailException(email);
        }
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return mapperInstance.toPatientDto(patient);
    }
    public PatientDto findById(@NotNull Long id){
        Patient patientToFind = repository.findById(id).orElseThrow(PatientWithIdNotFoundException::new);
        return mapperInstance.toPatientDto(patientToFind);
    }
    public PatientDto addPatient(@NonNull CreatePatientCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.email());

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(patientCommand.email());
        }

        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(emailNormalizer);
        repository.save(patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        String emailNormalizer = EmailValidator.normalize(email);

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(email);
        }
        Patient patientToDelete = repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        repository.delete(patientToDelete);
    }
    public void deleteById(@NonNull Long id){
        Patient patientToDelete = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        repository.delete(patientToDelete);
    }

    public PatientDto updatePatient(@NonNull String email, @NonNull CreatePatientCommand patientCommand) {
        Patient patient = mapperInstance.toEntity(patientCommand);
        patient.setEmail(EmailValidator.normalize(email));
        if (!emailValidator.validate(patient.getEmail())) {
            throw new InvalidEmailException(patient.getEmail());
        }
        repository.save(patient);
        return mapperInstance.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        if(newPassword.isBlank()){
            throw new InvalidPasswordException();
        }
        Patient patientToUpdate = repository.findByEmail(EmailValidator.normalize(email))
                .orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.setPassword(newPassword);
        repository.save(patientToUpdate);
    }
}
