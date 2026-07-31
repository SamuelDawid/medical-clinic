package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Doctor> resolveDoctors(Set<Long> doctorsIds){
        return doctorsIds.stream()
                .filter(Objects::nonNull)
                .map(doctorRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
    @Transactional
    public InstitutionDoctorsDto assignInstitutionToDoctorsById(Set<Long> doctorsIds, Institution institution){
        Set<Doctor> doctors = resolveDoctors(doctorsIds);
        doctors.forEach(doctor -> doctor.getInstitutions().add(institution));
        doctorRepository.saveAll(doctors);
        return new InstitutionDoctorsDto(institution.getName(),
                doctors.stream().map(doctorMapper::toSummaryDto)
                        .collect(Collectors.toSet()));
    }
    public Set<DoctorDto> showDoctorsByInstitution(Long institutionId){
        return doctorRepository.findByInstitutionsId(institutionId).stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toSet());
    }
}
