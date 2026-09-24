package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.AssignPatientToAppointmentCommand;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.enums.Timeframe;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.mappers.AppointmentMapper;
import com.samuelDawid.medical_clinic.model.Appointment;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.repository.AppointmentsRepository;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.searchCriteria.AppointmentSearchCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.samuelDawid.medical_clinic.model.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private AppointmentsRepository repository;
    private AppointmentMapper mapper;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentService service;
    List<Appointment> appointmentList;
    List<Doctor> doctorList;
    List<Patient> patientList;

    @BeforeEach
    void setUp() {

        this.repository = Mockito.mock(AppointmentsRepository.class);
        this.mapper = Mappers.getMapper(AppointmentMapper.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.service = new AppointmentService(repository, mapper, doctorRepository, patientRepository);
        this.appointmentList = TestDataFactory.threeAppointments();
        this.doctorList = TestDataFactory.threeDoctors();
        this.patientList = TestDataFactory.threePatients();
    }

    @Test
    void search_WhenNoCriteriaGiven_ShouldReturnPageWithAllAppointments() {
        //given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria = new AppointmentSearchCriteria(null, null, null, null, null, null);
        Page<Appointment> page = new PageImpl<>(appointmentList, pageable, 3);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable))).thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        assertAll(
                () -> assertEquals(3, result.content().size()),
                () -> assertEquals(3, result.totalElements()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(FIRST_START, result.content().getFirst().startDateTime())
        );
    }

    @Test
    void search_WhenFilteringByPatient_ShouldReturnPageWithPatientSpecification() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(PATIENT_ID, null, null, null, null, null);
        Page<Appointment> page = new PageImpl<>(List.of(appointmentList.getFirst()), pageable, 1);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        verify(repository).findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable));
        AppointmentDto first = result.content().getFirst();
        Assertions.assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(20, result.pageSize()),
                () -> assertEquals(1L, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals("Anna Kowalska", first.patientName()),
                () -> assertEquals(DOCTOR_NAME, first.doctorName()),
                () -> assertEquals(FIRST_START, first.startDateTime())
        );
    }

    @Test
    void search_WhenFilteringByDoctorAndSpecialization_ShouldReturnPageWithBothSpecifications() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, DOCTOR_ID, SPECIALIZATION, null, null, null);
        Page<Appointment> page = new PageImpl<>(List.of(appointmentList.getFirst(), appointmentList.get(1)), pageable, 2);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        Assertions.assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertEquals(DOCTOR_NAME, result.content().getFirst().doctorName()),
                () -> assertEquals(DOCTOR_NAME, result.content().get(1).doctorName())
        );
    }

    @Test
    void search_WhenFilteringByDateRange_ShouldPassStartsBetweenSpecificationToRepository() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        LocalDateTime from = LocalDateTime.of(2026, 9, 15, 10, 30);
        LocalDateTime to = LocalDateTime.of(2026, 9, 15, 18, 30);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, null, null, from, to, null);
        Page<Appointment> page = new PageImpl<>(appointmentList, pageable, 3);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        ArgumentCaptor<Specification<Appointment>> captor = ArgumentCaptor.captor();
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        verify(repository).findAll(captor.capture(), eq(pageable));
        Assertions.assertAll(
                () -> assertNotNull(captor.getValue()),
                () -> assertEquals(appointmentList.size(), result.content().size())
        );
    }

    @Test
    void search_WhenFilteringByTimeframe_ShouldReturnPageWithTimeframeSpecification() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, null, null, null, null, Timeframe.PAST);
        // wizyty z factory są z 15.09.2026, czyli już w przeszłości
        Page<Appointment> page = new PageImpl<>(appointmentList.subList(0, 2), pageable, 2);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        Assertions.assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertTrue(result.content().getFirst().endDateTime().isBefore(LocalDateTime.now()))
        );
    }

    @Test
    void search_WhenNothingMatches_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria searchCriteria =
                new AppointmentSearchCriteria(3L, null, null, null, null, Timeframe.ALL);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable))).thenReturn(Page.empty(pageable));
        //When
        PageDto<AppointmentDto> result = service.search(searchCriteria, pageable);
        //Then
        assertAll(
                () -> assertNotNull(result.content()),
                () -> assertTrue(result.content().isEmpty()),
                () -> assertEquals(0, result.totalElements()),
                () -> assertEquals(0, result.totalPages()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(20, result.pageSize())
        );
        verify(repository).findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable));
    }

    @Test
    void findById_whenAppointmentExists_ShouldReturnMatchingAppointmentDto() {
        //Given
        Long existingId = 1L;
        Appointment existingAppointment = appointmentList.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(existingAppointment));
        //When
        AppointmentDto result = service.findById(existingId);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Anna Kowalska", result.doctorName()),
                () -> assertEquals("Anna Kowalska", result.patientName()),
                () -> assertEquals(LocalDateTime.of(2026, 9, 15, 15, 30), result.startDateTime()),
                () -> assertEquals(LocalDateTime.of(2026, 9, 15, 16, 15), result.endDateTime())
        );
        verify(repository).findById(existingId);
    }

    @Test
    void findById_whenAppointmentDoesNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        AppointmentDoesNotExistsException exception = assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.findById(id));
        assertTrue(exception.getMessage().contains("Appointment Does Not Exists"));
    }

    @Test
    void create_whenDoctorAndPatientExists_ShouldSaveAndReturnAppointmentDto() {
        //Given
        Long existingDoctorId = 1L;
        Long existingPatientId = 2L;
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                existingPatientId,
                existingDoctorId,
                LocalDateTime.of(2030, 9, 15, 15, 30),
                LocalDateTime.of(2030, 9, 15, 16, 15)
        );
        Patient patient = patientList.get(1);
        Doctor doctor = doctorList.getFirst();
        Appointment appointment = new Appointment(
                LocalDateTime.of(2030, 9, 15, 15, 30),
                LocalDateTime.of(2030, 9, 15, 16, 15),
                patient,
                doctor
        );
        when(doctorRepository.findByIdForUpdate(existingDoctorId)).thenReturn(Optional.of(doctorList.getFirst()));
        when(patientRepository.findById(existingPatientId)).thenReturn(Optional.of(patientList.get(1)));
        when(repository.save(any())).thenReturn(appointment);
        //When
        AppointmentDto result = service.create(command);
        //Then
        verify(repository).save(any(Appointment.class));
        Assertions.assertAll(
                () -> assertEquals("Anna Kowalska", result.doctorName()),
                () -> assertEquals("Piotr Nowak", result.patientName()),
                () -> assertEquals(LocalDateTime.of(2030, 9, 15, 15, 30), result.startDateTime()),
                () -> assertEquals(LocalDateTime.of(2030, 9, 15, 16, 15), result.endDateTime())
        );
    }

    @Test
    void create_whenDoctorDoesNotExistsAndPatientExists_ShouldThrowDoctorNotFoundException() {
        //Given
        Long doctorId = 666L;
        Long existingPatientId = 2L;
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                existingPatientId,
                doctorId,
                LocalDateTime.of(2030, 9, 15, 15, 30),
                LocalDateTime.of(2030, 9, 15, 16, 15)
        );
        when(doctorRepository.findByIdForUpdate(doctorId)).thenReturn(Optional.empty());
        when(patientRepository.findById(existingPatientId)).thenReturn(Optional.of(patientList.get(1)));
        //When + Then
        assertThrows(DoctorNotFoundException.class,
                () -> service.create(command));
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void create_whenDoctorExistsAndPatientDoesNotExists_ShouldThrow() {
        //Given
        Long doctorId = 1L;
        Long existingPatientId = 666L;
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                existingPatientId,
                doctorId,
                LocalDateTime.of(2030, 9, 15, 15, 30),
                LocalDateTime.of(2030, 9, 15, 16, 15)
        );
        when(doctorRepository.findByIdForUpdate(doctorId)).thenReturn(Optional.of(doctorList.getFirst()));
        when(patientRepository.findById(existingPatientId)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(PatientNotFoundException.class,
                () -> service.create(command));
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void create_WhenAppointmentHasIncorrectTime_ShouldThrowInvalidTimeOfTheAppointmentException() {
        //Given
        Long existingDoctorId = 1L;
        Long existingPatientId = 2L;
        CreateAppointmentCommand appointmentCommand = new CreateAppointmentCommand(
                existingPatientId,
                existingDoctorId,
                LocalDateTime.of(2030, 9, 15, 15, 12),
                LocalDateTime.of(2030, 9, 15, 15, 55)

        );
        when(doctorRepository.findByIdForUpdate(existingDoctorId)).thenReturn(Optional.of(doctorList.getFirst()));
        when(patientRepository.findById(existingPatientId)).thenReturn(Optional.of(patientList.get(1)));
        //When + Then
        InvalidTimeOfTheAppointmentException exception = assertThrows(InvalidTimeOfTheAppointmentException.class,
                () -> service.create(appointmentCommand));
        assertTrue(exception.getMessage().contains("Invalid Time Of The Appointment"));
    }

    @Test
    void create_WhenAppointmentHasIncorrectDate_ShouldThrowInvalidTimeOfTheAppointmentException() {
        Long existingDoctorId = 1L;
        Long existingPatientId = 2L;
        CreateAppointmentCommand appointmentCommand = new CreateAppointmentCommand(
                existingPatientId,
                existingDoctorId,
                LocalDateTime.of(2026, 4, 15, 15, 12),
                LocalDateTime.of(2026, 4, 15, 15, 55)

        );
        when(doctorRepository.findByIdForUpdate(existingDoctorId)).thenReturn(Optional.of(doctorList.getFirst()));
        when(patientRepository.findById(existingPatientId)).thenReturn(Optional.of(patientList.get(1)));
        //When + Then
        InvalidDateOfAppointmentException exception = assertThrows(InvalidDateOfAppointmentException.class,
                () -> service.create(appointmentCommand));
        assertTrue(exception.getMessage().contains("Date must be in the future"));
    }

    @Test
    void removePatientFromVisit_WhenAppointmentAndPatientExists_ShouldThrowInvalidDateOfAppointmentException() {
        //Given
        Appointment appointment = appointmentList.getFirst();
        Long appointmentId = 1L;
        when(repository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        //When
        service.removePatientFromVisit(appointmentId);
        //Then
        Assertions.assertAll(
                () -> assertNull(appointment.getPatient()),
                () -> assertNotNull(appointment.getDoctor())
        );
    }

    @Test
    void removePatientFromVisit_WhenAppointmentDoesNotExistAndPatientExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.removePatientFromVisit(id));
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentAndPatientExists_ShouldAssignAndReturnAppointmentDto() {
        //Given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Doctor doctor = doctorList.getFirst();
        Appointment appointment = new Appointment(
                LocalDateTime.of(2030, 9, 15, 10, 45),
                LocalDateTime.of(2030, 9, 15, 11, 15),
                null,
                doctor
        );
        AssignPatientToAppointmentCommand assignPatientToAppointmentCommand = new AssignPatientToAppointmentCommand(patientId, appointmentId);
        when(repository.findWithLockById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientList.getFirst()));

        //When
        AppointmentDto result = service.assignPatientToAppointment(assignPatientToAppointmentCommand);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Anna Kowalska", result.doctorName()),
                () -> assertEquals("Anna Kowalska", result.patientName()),
                () -> assertEquals(LocalDateTime.of(2030, 9, 15, 10, 45), result.startDateTime()),
                () -> assertEquals(LocalDateTime.of(2030, 9, 15, 11, 15), result.endDateTime())
        );
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentAlreadyTakenAndPatientExists_ShouldThrowAppointmentAlreadyTakenException() {
        //Given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Appointment appointment = appointmentList.getFirst();
        AssignPatientToAppointmentCommand assignPatientToAppointmentCommand = new AssignPatientToAppointmentCommand(patientId, appointmentId);
        when(repository.findWithLockById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientList.getFirst()));
        //Then + When
        AppointmentAlreadyTakenException exception = assertThrows(AppointmentAlreadyTakenException.class,
                () -> service.assignPatientToAppointment(assignPatientToAppointmentCommand));
        assertTrue(exception.getMessage().contains("This Appointment is already taken"));
    }

    @Test
    void delete_WhenAppointmentExists_ShouldRemoveAppointment() {
        //Given
        Long existingId = 1L;
        Appointment appointment = appointmentList.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(appointment));
        //When + Then
        service.delete(existingId);
        verify(repository).delete(appointment);
    }

    @Test
    void delete_WhenAppointmentDosNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.delete(id));
        verify(repository, never()).delete(any(Appointment.class));
    }
}