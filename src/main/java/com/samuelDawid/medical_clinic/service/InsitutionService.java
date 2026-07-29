package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.InstitutionMapper;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsitutionService {
    private final InstitutionRepository repository;
    private final InstitutionMapper mapper;

    public List<InstitutionDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public InstitutionDto findById(@NonNull Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(InstitutionNotFoundException::new);
    }

    public InstitutionDto create(@NonNull CreateInstitutionCommand command) {
        Institution institution = mapper.toEntity(command);
        if (repository.findAll().contains(institution)) {
            throw new InstitutionAlreadyExistsException();
        }
        repository.save(institution);
        return mapper.toDto(institution);
    }

    @Transactional
    public InstitutionDto update(@NonNull Long id, @NonNull PatchInsitutionCommand command) {
        Institution institution = repository.findById(id).orElseThrow(InstitutionNotFoundException::new);
        if (command.Name() != null) {
            institution.setName(command.Name());
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
    public void delete(@NonNull Long id){
        Institution institution = repository.findById(id).orElseThrow(InstitutionNotFoundException::new);
        repository.delete(institution);
    }
}
