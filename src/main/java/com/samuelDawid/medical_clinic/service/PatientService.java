package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.model.Patient;
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
    public List<Patient> all() {
        return repository.findAll();
    }

    public Patient findByEmail(@NonNull String email) {
        EmailValidator.normalize(email);
        return repository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addNewPatient(@NonNull Patient patient) {
        if(!emailValidator.validate(patient.getEmail())){
            throw new InvalidEmailException(patient.getEmail());
        }
        patient.setEmail(EmailValidator.normalize(patient.getEmail()));
        return repository.create(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        EmailValidator.normalize(email);
        repository.delete(email);
    }

    public Patient update(@NonNull String email, @NonNull Patient patient) {
        if(!emailValidator.validate(patient.getEmail())){
            throw new InvalidEmailException(patient.getEmail());
        }
        return repository.update(email, patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        Patient patientToUpdate = repository.findByEmail(EmailValidator.normalize(email)).orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.setPassword(newPassword);
    }
}
