package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository repository;
    private final DoctorMapper mapper;
    private final UserPatcher userPatcher;

    public List<DoctorDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public DoctorDto findById(@NonNull Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(DoctorNotFoundException::new);
    }

    public DoctorDto create(@NonNull CreateDoctorCommand command) {
        Doctor doctor = mapper.toEntity(command);
        if (repository.findAll().contains(doctor)) {
            throw new InstitutionAlreadyExistsException();
        }
        repository.save(doctor);
        return mapper.toDto(doctor);
    }

    @Transactional
    public DoctorDto update(@NonNull Long id, @NonNull PatchDoctorCommand command) {
        Doctor doctor = repository.findById(id).orElseThrow(DoctorNotFoundException::new);
        if (command.medicalSpecialty() != null) {
            doctor.setMedicalSpecialty(command.medicalSpecialty());
        }
        if (command.user() != null) {
            userPatcher.apply(command.user(), doctor.getUser());
        }

        return mapper.toDto(doctor);
    }

    public void delete(@NonNull Long id) {
        Doctor doctor = repository.findById(id).orElseThrow(DoctorNotFoundException::new);
        repository.delete(doctor);
    }
}
