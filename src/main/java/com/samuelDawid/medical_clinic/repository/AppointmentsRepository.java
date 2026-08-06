package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAllByPatientId(Long id, Pageable pageable);
    Set<Appointment> findByDoctorAndDateAndStartTimeLessThanAndEndTimeGreaterThan(Doctor doctor,
                                                                                  LocalDate date,
                                                                                  LocalTime newEndTime,
                                                                                  LocalTime newStartTime

    );

}
