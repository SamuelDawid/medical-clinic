package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.PatchUserCommand;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.exceptions.PatientAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.PatientWithIdNotFoundException;
import com.samuelDawid.medical_clinic.mappers.PatientMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.repository.UserRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private PatientService patientService;
    private PatientRepository repository;
    private UserRepository userRepository;
    private EmailValidator emailValidator;
    private PatientMapper patientMapper;
    private UserMapper userMapper;
    private UserPatcher userPatcher;
    private List<Patient> seedData;

    @BeforeEach
    void setUp() {
        this.repository = Mockito.mock(PatientRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.emailValidator = new EmailValidator();
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userPatcher = new UserPatcher();
        this.patientService = new PatientService(repository, userRepository, emailValidator, patientMapper, userMapper, userPatcher);
        seedData = TestDataFactory.threePatients();
    }

    @Test
    void findAll_WhenThreePatientsExist_ShouldReturnPageWithAllThree() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(seedData, pageable, 3);
        when(repository.findAll(pageable)).thenReturn(patientPage);
        //When
        PageDto<PatientDto> result = patientService.findAll(pageable);
        //Then
        PatientDto first = result.content().getFirst();
        Assertions.assertAll(
                () -> assertNotNull(result.content()),
                () -> assertEquals(3, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(3L, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals(10, result.pageSize()),
                () -> assertEquals("Anna", first.userDto().firstName()),
                () -> assertEquals("Kowalska", first.userDto().lastName()),
                () -> assertEquals("anna.kowalska@test.pl", first.userDto().email()),
                () -> assertEquals("500100200", first.phoneNumber()),
                () -> assertEquals(LocalDate.of(1990, 3, 15), first.birthDay()),
                () -> assertEquals(1L, first.id())

        );
        verify(repository).findAll(pageable);
    }

    @Test
    void findById_WhenPatientExists_ShouldReturnMatchingPatientDto() {
        //Given
        Long existingId = 2L;
        when(repository.findById(existingId)).thenReturn(Optional.of(seedData.get(1)));
        //When
        PatientDto result = patientService.findById(existingId);
        //Then
        Assertions.assertAll(
                () -> assertEquals(LocalDate.of(1985, 7, 22), result.birthDay()),
                () -> assertEquals("500300400", result.phoneNumber()),
                () -> assertEquals("Piotr", result.userDto().firstName()),
                () -> assertEquals("Nowak", result.userDto().lastName()),
                () -> assertEquals("piotr.nowak@test.pl", result.userDto().email()),
                () -> assertEquals(existingId, result.id())
        );
        verify(repository).findById(existingId);
    }

    @Test
    void findById_WhenPatientWithIdDoesNotExist_ShouldThrowPatientWithIdNotFoundException() {
        //Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        PatientWithIdNotFoundException exception = assertThrows(PatientWithIdNotFoundException.class,
                () -> patientService.findById(id));
        assertTrue(exception.getMessage().contains("Patient with provided id not found"));
    }

    @Test
    void create_WhenEmailIsValid_ShouldSaveAndReturnPatientDto() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "  MariA.wisNiewska@test.PL ",
                "testUser3");
        CreatePatientCommand command = new CreatePatientCommand("ABC333333", LocalDate.of(2001, 11, 8), "500500600", createUserCommand);
        when(repository.save(any(Patient.class))).thenReturn(seedData.get(2));
        //When
        PatientDto result = patientService.create(command);
        //Then
        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(repository).save(captor.capture());
        Patient savedpatient = captor.getValue();
        Assertions.assertAll(
                () -> assertEquals(LocalDate.of(2001, 11, 8), result.birthDay()),
                () -> assertEquals("500500600", result.phoneNumber()),
                () -> assertEquals("Maria", result.userDto().firstName()),
                () -> assertEquals("Wisniewska", result.userDto().lastName()),
                () -> assertEquals("maria.wisniewska@test.pl", result.userDto().email())
        );
    }

    @Test
    void create_WhenEmailIsInvalid_ShouldThrowInvalidEmailException() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "  MariA.wisNiewska@@test.PL ",
                "testUser3");
        CreatePatientCommand command = new CreatePatientCommand("ABC333333",
                LocalDate.of(2001, 11, 8),
                "500500600",
                createUserCommand);
        //when + Then
        InvalidEmailException exception = Assertions.assertThrows(
                InvalidEmailException.class, () -> patientService.create(command));
        assertTrue(exception.getMessage().contains("Invalid email: "));
    }

    @Test
    void create_WhenEmailAlreadyExists_ShouldThrowPatientAlreadyExistsException() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        CreatePatientCommand command = new CreatePatientCommand("ABC333333",
                LocalDate.of(2001, 11, 8),
                "500500600",
                createUserCommand);
        when(userRepository.existsByEmail("maria.wisniewska@test.pl")).thenReturn(true);
        //when + Then
        PatientAlreadyExistsException exception = Assertions.assertThrows(
                PatientAlreadyExistsException.class, () -> patientService.create(command));
        assertTrue(exception.getMessage().contains("Patient with email maria.wisniewska@test.pl already exists"));
        verify(repository, never()).save(any());
    }

    @Test
    void updatePatient_WhenPatientExists_ShouldReturnUpdatedPatientDto() {
        //Given
        PatchUserCommand userCommand = new PatchUserCommand(null, null, "eXamPleChange@example.COM");
        PatchPatientCommand command = new PatchPatientCommand(null, null, "+48 609567865", userCommand);
        when(repository.findById(1L)).thenReturn(Optional.of(seedData.getFirst()));
        when(repository.save(any(Patient.class))).thenReturn(seedData.getFirst());
        //When
        PatientDto result = patientService.updatePatient(1L, command);
        //Then
        Assertions.assertAll(
                () -> assertEquals("examplechange@example.com", result.userDto().email()),
                () -> assertEquals("+48 609567865", result.phoneNumber())
        );
    }

    @Test
    void delete_whenPatientExists_ShouldRemovedPatient() {
        //Given
        Long existingId = 1L;
        Patient existingPatient = seedData.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(existingPatient));
        //When
        patientService.deleteById(existingId);
        //Then
        verify(repository).delete(existingPatient);
    }

    @Test
    void delete_WhenPatientDoesNotExists_ShouldThrowPatientWithIdNotFoundException() {
        //Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        PatientWithIdNotFoundException exception = assertThrows(PatientWithIdNotFoundException.class,
                () -> patientService.deleteById(id));
        assertTrue(exception.getMessage().contains("Patient with provided id not found"));
        verify(repository, never()).delete(any());
    }

    @Test
    void updatePassword_WhenPatientExists_ShouldChangeUserPassword() {
        //Given
        String newPassword = "newPassword123";
        Long existingId = 2L;
        User testPassword = TestDataFactory.buildUser(3L, "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        Patient patient = TestDataFactory.buildPatient(2L, "cardTest", LocalDate.of(1995, 3, 1), "123123", testPassword);
        when(repository.findById(existingId)).thenReturn(Optional.of(patient));
        //When
        patientService.updatePassword(newPassword, existingId);
        //Then
        verify(repository).findById(existingId);
        assertEquals("newPassword123", patient.getUser().getPassword());
    }
}