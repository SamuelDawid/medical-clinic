package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> all() {
        return repository.findAll();
    }

    public Patient findByEmail(@NonNull String email) {
        return repository.findByEmail(email).orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addNewPatient(@NonNull Patient patient) {
        return repository.create(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        repository.delete(email);
    }

    public Patient update(@NonNull String id, @NonNull Patient patient) {
        return repository.update(id, patient);
    }
}
