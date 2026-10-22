package com.medicalclinic.adapter.appointment;

import appointment.AppointmentRepositoryPort;
import com.medicalclinic.adapter.AdapterControllerCommon;
import com.medicalclinic.adapter.doctor.DoctorJpaRepository;
import com.medicalclinic.adapter.patient.PatientJpaRepository;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.critera.AppointmentSearchCriteria;
import internalCommandsAndDtos.critera.AvailableAppointmentCriteria;
import lombok.RequiredArgsConstructor;
import models.AppointmentPOJO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.medicalclinic.adapter.appointment.AppointmentSpecifications.*;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {
    private final AppointmentJpaRepository jpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final PatientJpaRepository patientJpaRepository;
    private final AppointmentEntityMapper mapper;

    @Override
    @Transactional
    public AppointmentPOJO save(AppointmentPOJO pojo) {
        AppointmentEntity entity = mapper.toEntity(pojo);
        entity.setDoctor(pojo.getDoctor() == null ? null
                : doctorJpaRepository.getReferenceById(pojo.getDoctor().getId()));
        entity.setPatient(pojo.getPatient() == null ? null
                : patientJpaRepository.getReferenceById(pojo.getPatient().getId()));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<AppointmentPOJO> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    // Blokada pesymistyczna WYMAGA transakcji (inaczej TransactionRequiredException).
    // Uwaga: kończy się ona razem z tą metodą – pełna ochrona przed wyścigiem wymaga
    // transakcji na cały use case (lista zadań: temat transakcji z devem).
    @Override
    @Transactional
    public Optional<AppointmentPOJO> findWithLockById(Long id) {
        return jpaRepository.findWithLockById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<AppointmentPOJO> findAll(PageQuery pageQuery) {
        return toPageResult(jpaRepository.findAll(AdapterControllerCommon.toPageable(pageQuery)));
    }

    @Override
    public PageResult<AppointmentPOJO> findAllByPatientId(Long patientId, PageQuery pageQuery) {
        return toPageResult(jpaRepository.findAllByPatientId(patientId, AdapterControllerCommon.toPageable(pageQuery)));
    }

    @Override
    public PageResult<AppointmentPOJO> search(AppointmentSearchCriteria criteria, PageQuery pageQuery) {
        Specification<AppointmentEntity> specification = Specification.allOf(Stream.of(
                hasPatient(criteria.patientId()),
                hasDoctor(criteria.doctorId()),
                hasSpecialization(criteria.specialization()),
                startsBetween(criteria.from(), criteria.to()),
                inTimeframe(criteria.timeframe())
        ).filter(Objects::nonNull).toList());
        return toPageResult(jpaRepository.findAll(specification, AdapterControllerCommon.toPageable(pageQuery)));
    }

    @Override
    public PageResult<AppointmentPOJO> searchAvailable(AvailableAppointmentCriteria criteria, PageQuery pageQuery) {
        Specification<AppointmentEntity> specification = Specification.allOf(Stream.of(
                isFree(),
                hasDoctor(criteria.doctorId()),
                hasSpecialization(criteria.specialization()),
                startsBetween(criteria.from(), criteria.to())
        ).filter(Objects::nonNull).toList());
        return toPageResult(jpaRepository.findAll(specification, AdapterControllerCommon.toPageable(pageQuery)));
    }

    @Override
    public boolean existsOverlapping(Long doctorId, LocalDateTime start, LocalDateTime end) {
        // Kolejność celowa: Spring Data dostaje najpierw koniec nowej wizyty, potem początek
        return jpaRepository.existsByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(doctorId, end, start);
    }

    private PageResult<AppointmentPOJO> toPageResult(Page<AppointmentEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }
}