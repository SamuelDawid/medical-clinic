package com.medicalclinic.adapter.patient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity,Long> {
}
