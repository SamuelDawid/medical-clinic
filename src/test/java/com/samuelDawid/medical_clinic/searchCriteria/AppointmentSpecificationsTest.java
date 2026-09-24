package com.samuelDawid.medical_clinic.searchCriteria;

import com.samuelDawid.medical_clinic.enums.Timeframe;
import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.repository.AppointmentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.samuelDawid.medical_clinic.searchCriteria.AppointmentSpecifications.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class AppointmentSpecificationsTest {
    @Autowired
    AppointmentsRepository repository;
    List<Appointment> appointments;

    @BeforeEach
    void setUp() {
        appointments = TestDataFactory.threeAppointments();
        repository.saveAll(appointments);
    }

    @Test
    void hasPatient_WhenPatientIdGiven_ShouldMatchOnlyAppointmentsOfThatPatient() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Long patientId = 1L;
        //When
        List<Appointment> result = repository.findAll(hasPatient(patientId), pageable).getContent();
        //Then
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(patientId, result.getFirst().getPatient().getId()),
                () -> assertEquals("Anna", result.getFirst().getPatient().getUser().getFirstName()),
                () -> assertEquals("Kowalska", result.getFirst().getPatient().getUser().getLastName())
        );
    }

    @Test
    void hasDoctor_WhenDoctorIdGiven_ShouldMatchOnlyAppointmentsOfThatDoctor() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        Long doctorId = 1L;
        //When
        List<Appointment> result = repository.findAll(hasDoctor(doctorId), pageable).getContent();
        //Then
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(doctorId, result.getFirst().getDoctor().getId()),
                () -> assertEquals("SpecialityOne", result.getFirst().getDoctor().getMedicalSpecialty())
        );
    }

    @Test
    void hasSpecialization_WhenSpecializationGiven_ShouldMatchOnlyAppointmentsWithThatSpecialisation() {
        //When
        List<Appointment> result = repository.findAll(hasSpecialization("SpecialityOne"));
        //Then
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals("SpecialityOne", result.getFirst().getDoctor().getMedicalSpecialty())
        );
    }

    @Test
    void startsBetween_WhenBothBoundsGiven_ShouldMatchAppointmentsStartingInsideTheRange() {
        //Given
        LocalDateTime from = LocalDateTime.of(2026, 9, 15, 11, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 15, 16, 0);
        //When
        List<Appointment> result = repository.findAll(startsBetween(from, to));
        //Then
        assertEquals(2, result.size());
    }

    @Test
    void startsBetween_WhenOnlyFromGiven_ShouldMatchAppointmentsStartingAtOrAfterIt() {
        //Given
        LocalDateTime from = LocalDateTime.of(2026, 9, 15, 15, 0);
        //When
        List<Appointment> result = repository.findAll(startsBetween(from, null));
        //Then
        assertEquals(1, result.size());
    }

    @Test
    void startsBetween_WhenOnlyToGiven_ShouldMatchAppointmentsStartingBeforeIt() {
        //Given
        LocalDateTime to = LocalDateTime.of(2026, 9, 15, 12, 0);
        //When
        List<Appointment> result = repository.findAll(startsBetween(null, to));
        //Then
        assertEquals(2, result.size());
    }

    @Test
    void inTimeframe_WhenPastGiven_ShouldMatchOnlyAlreadyFinishedAppointments() {
        //When
        List<Appointment> result = repository.findAll(inTimeframe(Timeframe.PAST));
        //Then
        assertEquals(3, result.size());
    }

    @Test
    void isFree_ShouldMatchOnlyAppointmentsWithoutPatient() {
        //Given
        repository.save(new Appointment(
                LocalDateTime.of(2030, 9, 17, 11, 0),
                LocalDateTime.of(2030, 9, 17, 11, 45),
                null,
                TestDataFactory.threeDoctors().getFirst()));
        //When
        List<Appointment> result = repository.findAll(isFree());
        //Then
        assertEquals(1, result.size());
    }
}
