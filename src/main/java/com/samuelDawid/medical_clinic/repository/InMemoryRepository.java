package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryRepository implements PatientRepository{
    @Override
    public boolean create(Patient patient) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() {
        return List.of();
    }

    @Override
    public Patient update(String id, Patient patient) {
        return null;
    }
}
