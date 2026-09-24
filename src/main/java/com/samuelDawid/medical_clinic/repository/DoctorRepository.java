package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorSummaryDto;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.searchCriteria.DoctorSearchCriteria;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserEmail(String email);
    Set<Doctor> findByInstitutionsId(Long institutionId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "jakarta.persistence.lock.timeout",value = "5000")})
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Optional<Doctor> findByIdForUpdate(@Param("id") Long id);
    PageDto<DoctorSummaryDto> getDoctorsFromSpecificSpeciality(DoctorSearchCriteria criteria, Pageable pageable);
}
