package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.InstitutionMapper;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(InstitutionNotFoundException::new);
    }
    @Transactional
    public InstitutionDto create(@NonNull CreateInstitutionCommand command) {
        Institution institution = mapper.toEntity(command);
        if (repository.findByName(command.name())
                .isPresent()) {
            throw new InstitutionAlreadyExistsException();
        }
        repository.save(institution);
        return mapper.toDto(institution);
    }
    @Transactional
    public InstitutionDoctorsDto addDoctorsToInstitution(Set<Long> doctorsIdList, Long id) {
        Institution institution = repository.findById(id)
                .orElseThrow(InstitutionNotFoundException::new);
        return affiliationService.assignInstitutionToDoctorsById(doctorsIdList, institution);
    }
    @Transactional(readOnly = true)
    public Set<DoctorDto> showDoctors(Long institutionId) {
        return affiliationService.showDoctorsByInstitution(institutionId);
    }

    @Transactional
    public InstitutionDto update(@NonNull Long id, @NonNull PatchInsitutionCommand command) {
        Institution institution = repository.findById(id)
                .orElseThrow(InstitutionNotFoundException::new);
        institution.update(command);
        return mapper.toDto(institution);
    }
    @Transactional
    public void delete(@NonNull Long id) {
        Institution institution = repository.findById(id)
                .orElseThrow(InstitutionNotFoundException::new);
        repository.delete(institution);
    }

}
