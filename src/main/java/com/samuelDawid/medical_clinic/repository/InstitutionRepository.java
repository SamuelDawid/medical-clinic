package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.institution.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution,Long> {
    Optional<Institution> findByName(String name);
}
