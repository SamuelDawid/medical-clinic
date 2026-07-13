package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.repository.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    InMemoryPatientRepository repository;
    @InjectMocks
    PatientService service;
    static Patient patientTest;
    static Patient patientTestTwo;
    @BeforeAll
    static void setUp() {
         patientTest = new Patient("bob@example.com",
                "password",
                "123-123",
                "Bob",
                "example",
                LocalDate.of(2000, 6, 15),
                "555-555-555");
        patientTestTwo = new Patient("anna@example.com",
                "password",
                "321-321",
                "Anna",
                "example",
                LocalDate.of(1996, 3, 15),
                "666-666-666");
    }

}