package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Patient;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryRepository implements PatientRepository{
   Map<String,Patient> repository = new HashMap<>();

    @Override
    public Patient create(@NonNull Patient patient) {
        if(repository.containsValue(patient)) throw new IllegalArgumentException("Patient already exists");
        return repository.put(patient.getEmail(),patient);
    }

    @Override
    public boolean delete(@NonNull String email) {
       if(!repository.containsKey(email)) return false;
       repository.remove(email);
       return true;
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
    public Patient update(@NonNull String id,@NonNull Patient patient) {

        return repository.put(id,patient);
    }
}
