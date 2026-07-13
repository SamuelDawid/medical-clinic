package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    boolean create(Patient patient);
    boolean delete(String id);
    Optional<Patient> findByEmail(String email);
    List<Patient> findAll();
    Patient update(String id, Patient patient);

}
