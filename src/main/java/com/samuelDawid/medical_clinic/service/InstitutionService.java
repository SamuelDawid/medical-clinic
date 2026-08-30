package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInstitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.InstitutionMapper;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository repository;
    private final InstitutionMapper mapper;
    private final AffiliationService affiliationService;

    @Transactional(readOnly = true)
    public PageDto<InstitutionDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public InstitutionDto findById(@NonNull Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Transactional
    public InstitutionDto create(@NonNull CreateInstitutionCommand command) {
        log.info("Creating Institution");
        Institution institution = mapper.toEntity(command);
        if (repository.existsByName(command.name())) {
            log.warn("Institution with this ane already exists {}",command.name());
            throw new InstitutionAlreadyExistsException();
        }
        repository.save(institution);
        log.info("Created Institution with Id {}", institution.getId());
        return mapper.toDto(institution);
    }

    @Transactional
    public InstitutionDoctorsDto addDoctorsToInstitution(Set<Long> doctorsIdList, Long id) {
        log.info("Adding doctors with Ids {} to Institution {}", doctorsIdList, id);
        Institution institution = findOrThrow(id);
        return affiliationService.assignDoctorsByIdToInstitution(doctorsIdList, institution);
    }

    @Transactional(readOnly = true)
    public Set<DoctorDto> showDoctors(Long institutionId) {
        return affiliationService.showDoctorsByInstitution(institutionId);
    }

    @Transactional
    public InstitutionDto update(@NonNull Long id, @NonNull PatchInstitutionCommand command) {
        log.info("Updating institution {}", id);
        Institution institution = findOrThrow(id);
        institution.update(command);
        log.info("Institution {} updated successfully", id);
        return mapper.toDto(institution);
    }

    @Transactional
    public void delete(@NonNull Long id) {
        log.info("Removing institution {}", id);
        Institution institution = findOrThrow(id);
        repository.delete(institution);
        log.info("institution {} removed successfully", id);
    }

    private Institution findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Institution with id {} not found", id);
            return new InstitutionNotFoundException(id);
        });
    }
}
