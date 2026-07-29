package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    private final DoctorRepository repository;
    private final DoctorMapper mapper;
}
