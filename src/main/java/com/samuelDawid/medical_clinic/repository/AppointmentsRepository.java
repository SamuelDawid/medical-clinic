package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    Set<Appointment> findAllByPatientId(Long id);

    Set<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);

}
