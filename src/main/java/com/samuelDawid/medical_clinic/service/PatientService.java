package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
   private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

}
