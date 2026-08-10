package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAllByPatientId(Long id, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "jakarta.persistence.lock.timeout",value = "5000")})
    Optional<Appointment> findWithLockById(Long id);
    Set<Appointment> findByDoctorAndStartDateTimeLessThanAndEndDateTimeGreaterThan(Doctor doctor,
                                                                           LocalDateTime newEnd,
                                                                           LocalDateTime newStart

    );

}
