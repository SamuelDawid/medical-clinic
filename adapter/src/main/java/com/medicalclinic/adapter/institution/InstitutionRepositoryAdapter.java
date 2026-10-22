package com.medicalclinic.adapter.institution;

import com.medicalclinic.adapter.AdapterControllerCommon;
import institution.InstitutionRepositoryPort;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import lombok.RequiredArgsConstructor;
import models.institution.InstitutionPOJO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InstitutionRepositoryAdapter implements InstitutionRepositoryPort {
    private final InstitutionJpaRepository jpaRepository;
    private final InstitutionEntityMapper mapper;

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public PageResult<InstitutionPOJO> findAll(PageQuery pageQuery) {
        Page<InstitutionEntity> page = jpaRepository.findAll(AdapterControllerCommon.toPageable(pageQuery));
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public InstitutionPOJO save(InstitutionPOJO institutionPOJO) {
        InstitutionEntity saved = jpaRepository.save(mapper.toEntity(institutionPOJO));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<InstitutionPOJO> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Set<Long> findExistingIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Set.of();
        }
        return jpaRepository.findAllById(ids).stream()
                .map(InstitutionEntity::getId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jpaRepository.findById(id).ifPresent(institution -> {
            institution.getDoctors().forEach(doctor -> doctor.getInstitutions().remove(institution));
            jpaRepository.delete(institution);
        });
    }
}
