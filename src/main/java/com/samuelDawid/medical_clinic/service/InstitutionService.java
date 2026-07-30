package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.mappers.InstitutionMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository repository;
    private final DoctorRepository doctorRepository;
    private final InstitutionMapper mapper;
    private final DoctorMapper doctorMapper;

    public List<InstitutionDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public InstitutionDto findById(@NonNull Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(InstitutionNotFoundException::new);
    }

    public InstitutionDto create(@NonNull CreateInstitutionCommand command) {
        Institution institution = mapper.toEntity(command);
        if (repository.findByName(command.name()).isPresent()) {
            throw new InstitutionAlreadyExistsException();
        }
        repository.save(institution);
        return mapper.toDto(institution);
    }

    @Transactional
    public InstitutionDoctorsDto addDoctorsToInstitution(Set<Long> doctorsIdList, String institutionName) {
        Institution institution = repository.findByName(institutionName).orElseThrow(InstitutionNotFoundException::new);
        Set<Doctor> doctors = addDoctorsFromList(doctorsIdList);
        for (Doctor doc : doctors) {
            doc.getInstitutions().add(institution);
        }
        doctorRepository.saveAll(doctors);
        return new InstitutionDoctorsDto(institutionName,
                doctors.stream().map(doctorMapper::toSummaryDto)
                        .collect(Collectors.toSet()));
    }

    public Set<DoctorDto> showDoctors(String institutionName) {
        return doctorRepository.findByInstitutionsName(institutionName).stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public InstitutionDto update(@NonNull Long id, @NonNull PatchInsitutionCommand command) {
        Institution institution = repository.findById(id).orElseThrow(InstitutionNotFoundException::new);
        if (command.name() != null) {
            institution.setName(command.name());
        }
        if (command.city() != null) {
            institution.getAddress().setCity(command.city());
        }
        if (command.buildingNumber() != null) {
            institution.getAddress().setBuildingNumber(command.buildingNumber());
        }
        if (command.postCode() != null) {
            institution.getAddress().setPostCode(command.postCode());
        }
        if (command.street() != null) {
            institution.getAddress().setStreet(command.street());
        }

        return mapper.toDto(institution);
    }

    public void delete(@NonNull Long id) {
        Institution institution = repository.findById(id).orElseThrow(InstitutionNotFoundException::new);
        repository.delete(institution);
    }

    private Set<Doctor> addDoctorsFromList(Set<Long> doctors) {
        Set<Doctor> setOfDoctors = new HashSet<>();
        if (doctors.isEmpty()) {
            return setOfDoctors;
        }
        for (Long doctorId : doctors) {
            if (doctorId != null) {
                Optional<Doctor> doctor = doctorRepository.findById(doctorId);
                doctor.ifPresent(setOfDoctors::add);
            }
        }
        return setOfDoctors;
    }
}
