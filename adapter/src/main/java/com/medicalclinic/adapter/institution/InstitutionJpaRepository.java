package com.medicalclinic.adapter.institution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionJpaRepository extends JpaRepository<InstitutionEntity,Long> {
    boolean existsByName(String name);
}
