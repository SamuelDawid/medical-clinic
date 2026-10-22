package com.medicalclinic.adapter.patient;

import com.medicalclinic.adapter.AdapterControllerCommon;
import internalCommands.PageQuery;
import internalCommands.PageResult;
import lombok.RequiredArgsConstructor;
import models.PatientPOJO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import patient.PatientRepositoryPort;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepositoryPort {
    private final PatientJpaRepository repository;
    private final PatientEntityMapper mapper;

    @Override
    public PageResult<PatientPOJO> findAll(PageQuery pageQuery) {
        Page<PatientEntity> page = repository.findAll(AdapterControllerCommon.toPageable(pageQuery));
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public PatientPOJO save(PatientPOJO patientPOJO) {
        PatientEntity patient = mapper.toEntity(patientPOJO);
        PatientEntity saved = repository.save(patient);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PatientPOJO> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
