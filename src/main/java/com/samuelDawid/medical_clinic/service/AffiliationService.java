package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AffiliationService {
    private final DoctorRepository doctorRepository;
    private final InstitutionRepository institutionRepository;
    private final DoctorMapper doctorMapper;

    public Set<Institution> resolveInstitutions(Set<Long> institutionsIds) {
        return institutionsIds.stream()
                .filter(Objects::nonNull)
                .map(institutionRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<Doctor> resolveDoctors(Set<Long> doctorsIds) {
        return doctorsIds.stream()
                .filter(Objects::nonNull)
                .map(doctorRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
    @Transactional
    public void assignInstitutionsByIdToDoctor(Set<Long> institutionsId, Doctor doctor){
        Set<Institution> institutions = resolveInstitutions(institutionsId);
        checkIfAllInstitutionsWereAdded(institutionsId,institutions);
        if(!doctor.getInstitutions().isEmpty()){
            institutions.addAll(doctor.getInstitutions());
        }
        doctor.setInstitutions(institutions);
    }
    @Transactional
    public InstitutionDoctorsDto assignDoctorsByIdToInstitution(Set<Long> doctorsIds, Institution institution) {
        Set<Doctor> doctors = resolveDoctors(doctorsIds);
        checkIfAllDoctorsWereAdded(doctorsIds, doctors);
        doctors.forEach(doctor -> doctor.getInstitutions()
                .add(institution));
        doctorRepository.saveAll(doctors);
        log.info("Successfully added doctors to institution {}", institution.getId());
        return new InstitutionDoctorsDto(institution.getName(),
                doctors.stream()
                        .map(doctorMapper::toSummaryDto)
                        .collect(Collectors.toSet()));
    }

    @Transactional(readOnly = true)
    public Set<DoctorDto> showDoctorsByInstitution(Long institutionId) {
        return doctorRepository.findByInstitutionsId(institutionId)
                .stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toSet());
    }

    private void checkIfAllDoctorsWereAdded(Set<Long> doctorId, Set<Doctor> doctors) {
        Set<Long> resolvedIds = doctors.stream()
                .map(Doctor::getId)
                .collect(Collectors.toSet());
        for (Long id : doctorId) {
            if (!resolvedIds.contains(id)) {
                throw new DoctorNotFoundException(id);
            }
        }
    }
    private void checkIfAllInstitutionsWereAdded(Set<Long> institutionsId,Set<Institution> institutions){
        Set<Long>resolvedId = institutions.stream().map(Institution::getId).collect(Collectors.toSet());
        for (Long id : institutionsId){
            if(!resolvedId.contains(id)){
                throw new InstitutionNotFoundException(id);
            }
        }
    }
}
