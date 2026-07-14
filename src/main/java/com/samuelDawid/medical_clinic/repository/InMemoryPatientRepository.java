package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Patient;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPatientRepository implements PatientRepository {
    private final Map<String, Patient> repository = new ConcurrentHashMap<>();

    @Override
    public Patient create(@NonNull Patient patient) {
        if (repository.containsKey(patient.getEmail())) throw new IllegalArgumentException("Patient already exists");
        repository.put(patient.getEmail(), patient);
        return patient;
    }

    @Override
    public void delete(@NonNull String email) {
        if (!repository.containsKey(email)) throw new IllegalArgumentException("Patient does not exist");
        repository.remove(email);
    }

    @Override
    public Optional<Patient> findByEmail(@NonNull String email) {
        return Optional.ofNullable(repository.get(email));
    }

    @Override
    public List<Patient> findAll() {
        return repository.values().stream().toList();
    }

    @Override
    public Patient update(@NonNull String email, @NonNull Patient patient) {
        repository.put(email, patient);
        return repository.get(email);
    }

}
