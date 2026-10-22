package com.medicalclinic.adapter.appointment;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long>,
        JpaSpecificationExecutor<AppointmentEntity> {

    Page<AppointmentEntity> findAllByPatientId(Long patientId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    Optional<AppointmentEntity> findWithLockById(Long id);

    boolean existsByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(Long doctorId,
            LocalDateTime newEnd,
            LocalDateTime newStart);
}
