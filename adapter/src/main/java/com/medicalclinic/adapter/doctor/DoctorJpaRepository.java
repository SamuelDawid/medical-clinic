package com.medicalclinic.adapter.doctor;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity,Long> {
    Set<DoctorEntity> findByInstitutionsId(Long institutionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    @Query("SELECT d FROM DoctorEntity d WHERE d.id = :id")
    Optional<DoctorEntity> findByIdForUpdate(@Param("id") Long id);

    Page<DoctorEntity> findByMedicalSpecialtyIgnoreCase(String medicalSpecialty, Pageable pageable);
}
