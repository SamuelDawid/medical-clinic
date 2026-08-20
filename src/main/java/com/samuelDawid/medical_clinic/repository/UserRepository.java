package com.samuelDawid.medical_clinic.repository;

import com.samuelDawid.medical_clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
