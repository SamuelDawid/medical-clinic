package com.medicalclinic.adapter.doctor;

import com.medicalclinic.adapter.AdapterControllerCommon;
import com.medicalclinic.adapter.institution.InstitutionJpaRepository;
import doctor.DoctorRepositoryPort;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import lombok.RequiredArgsConstructor;
import models.DoctorPOJO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DoctorRepositoryAdapter implements DoctorRepositoryPort {
    private final DoctorJpaRepository jpaRepository;
    private final DoctorEntityMapper mapper;
    private final InstitutionJpaRepository institutionJpaRepository;

    @Override
    public PageResult<DoctorPOJO> findAll(PageQuery pageQuery) {
        Page<DoctorEntity> page = jpaRepository.findAll(AdapterControllerCommon.toPageable(pageQuery));

        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public DoctorPOJO save(DoctorPOJO doctor) {
        DoctorEntity entity = mapper.toEntity(doctor);
        entity.setInstitutions(doctor.getInstitutionsId().stream()
                .map(institutionJpaRepository::getReferenceById)
                .collect(Collectors.toSet()));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Optional<DoctorPOJO> findByIdForUpdate(Long id) {
        return jpaRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public Optional<DoctorPOJO> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<DoctorPOJO> findByMedicalSpecialtyIgnoreCase(String speciality, PageQuery pageQuery) {
        Page<DoctorEntity> page = jpaRepository.findByMedicalSpecialtyIgnoreCase(speciality, AdapterControllerCommon.toPageable(pageQuery));
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Set<DoctorPOJO> findAllById(Set<Long> ids) {
        return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).collect(Collectors.toSet());
    }

    @Override
    public Set<DoctorPOJO> findByInstitutionId(Long institutionId) {
        return jpaRepository.findByInstitutionsId(institutionId).stream().map(mapper::toDomain).collect(Collectors.toSet());
    }

    @Override
    public void saveAll(Set<DoctorPOJO> doctors) {
        doctors.forEach(this::save);
    }
}
