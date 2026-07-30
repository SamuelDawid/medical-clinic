package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserEmail(String email);

    Set<Doctor> findByInstitutionsName(String institutionName);
}
