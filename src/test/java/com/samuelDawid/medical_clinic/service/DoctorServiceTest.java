package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.PatchUserCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DoctorServiceTest {
    private DoctorService doctorService;
    private DoctorRepository repository;
    private UserRepository userRepository;
    private DoctorMapper mapper;
    private UserMapper userMapper;
    private UserPatcher userPatcher;
    private AffiliationService affiliationService;
    private EmailValidator emailValidator;
    private List<Doctor> doctorList;
    private List<Institution> institutionList;
    private InstitutionRepository institutionRepository;

    @BeforeEach
    void setUp() {
        this.repository = Mockito.mock(DoctorRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.emailValidator = new EmailValidator();
        this.mapper = Mappers.getMapper(DoctorMapper.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userPatcher = new UserPatcher();
        this.institutionRepository = Mockito.mock(InstitutionRepository.class);
        this.affiliationService = new AffiliationService(repository, institutionRepository, mapper);
        this.doctorService = new DoctorService(repository, userRepository, mapper, userMapper, userPatcher, affiliationService, emailValidator);
        this.doctorList = TestDataFactory.threeDoctors();
        this.institutionList = TestDataFactory.threeInstitutions();
    }

    @Test
    void findAll_WhenThreeDoctorsExist_ShouldReturnPageWithAllThree() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> doctorPage = new PageImpl<>(doctorList, pageable, 3);
        when(repository.findAll(pageable)).thenReturn(doctorPage);
        //When
        PageDto<DoctorDto> result = doctorService.findAll(pageable);
        //Then
        DoctorDto first = result.content().getFirst();
        Assertions.assertAll(
                () -> assertNotNull(result.content()),
                () -> assertEquals(3, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(3L, result.totalElements()),
                () -> assertEquals(10, result.pageSize()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals("Anna", first.userDto().firstName()),
                () -> assertEquals("Kowalska", first.userDto().lastName()),
                () -> assertEquals("anna.kowalska@test.pl", first.userDto().email()),
                () -> assertEquals("SpecialityOne", first.medicalSpecialty()),
                () -> assertEquals(1L, first.id())
        );
        verify(repository).findAll(pageable);
    }

    @Test
    void findAll_WhenNoDoctorsExist_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> page = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(page);
        //When
        PageDto<DoctorDto> result = doctorService.findAll(pageable);
        //Then
        Assertions.assertAll(
                () -> assertTrue(result.content().isEmpty()),
                () -> assertEquals(0, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(0L, result.totalElements()),
                () -> assertEquals(0, result.totalPages()),
                () -> assertEquals(10, result.pageSize())
        );
        verify(repository).findAll(pageable);
    }

    @Test
    void findById_WhenDoctorExists_ShouldReturnMatchingDoctorDto() {
        //Given
        Long existingId = 2L;
        Doctor existingDoctor = doctorList.get(1);

        existingDoctor.setInstitutions(Set.of(institutionList.getFirst()));
        when(repository.findById(existingId)).thenReturn(Optional.of(existingDoctor));
        //When
        DoctorDto result = doctorService.findById(existingId);
        //Then
        InstitutionDto first = result.institutionDto().iterator().next();
        Assertions.assertAll(
                () -> assertEquals("Piotr", result.userDto().firstName()),
                () -> assertEquals("Nowak", result.userDto().lastName()),
                () -> assertEquals("piotr.nowak@test.pl", result.userDto().email()),
                () -> assertEquals(2L, result.id()),
                () -> assertEquals("SpecialityTwo", result.medicalSpecialty()),
                () -> assertEquals(1L, first.id()),
                () -> assertEquals("Centrum Medyczne Alfa", first.name())

        );
        verify(repository).findById(existingId);
    }

    @Test
    void findById_WhenDoctorDoesNotExist_ShouldThrowDoctorNotFoundException() {
        //Given
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.findById(id));
        assertTrue(exception.getMessage().contains("Doctor 99 not found"));
    }

    @Test
    void create_WhenEmailIsValid_ShouldSaveAndReturnDoctorDto() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "  MariA.wisNiewska@test.PL ",
                "testUser3");
        CreateDoctorCommand createDoctorCommand = new CreateDoctorCommand(
                "SpecialityOne",
                createUserCommand,
                Set.of()
        );
        when(repository.save(any(Doctor.class))).thenReturn(doctorList.getLast());
        //When
        DoctorDto result = doctorService.create(createDoctorCommand);
        //Then
        ArgumentCaptor<Doctor> captor = ArgumentCaptor.forClass(Doctor.class);
        verify(repository).save(captor.capture());
        Doctor doctor = captor.getValue();
        Assertions.assertAll(
                () -> assertEquals("SpecialityOne", result.medicalSpecialty()),
                () -> assertEquals("Maria", result.userDto().firstName()),
                () -> assertEquals("Wisniewska", result.userDto().lastName()),
                () -> assertEquals("maria.wisniewska@test.pl", result.userDto().email()),
                () -> assertEquals("maria.wisniewska@test.pl", doctor.getUser().getEmail()),
                () -> assertEquals("testUser3", doctor.getUser().getPassword())
        );
    }

    @Test
    void create_WhenEmailFormatIsInvalid_ShouldThrowInvalidEmailException() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "  MariA.wisNiewska@@test.PL.com ",
                "testUser3");
        CreateDoctorCommand createDoctorCommand = new CreateDoctorCommand(
                "SpecialityOne",
                createUserCommand,
                Set.of()
        );
        when(repository.save(any(Doctor.class))).thenReturn(doctorList.getLast());
        //When + Then
        InvalidEmailException exception = Assertions.assertThrows(InvalidEmailException.class,
                () -> doctorService.create(createDoctorCommand));
        assertTrue(exception.getMessage().contains("Invalid email: "));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_WhenEmailAlreadyTaken_ShouldThrowDoctorAlreadyExistsException() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        CreateDoctorCommand createDoctorCommand = new CreateDoctorCommand(
                "SpecialityOne",
                createUserCommand,
                Set.of()
        );
        when(userRepository.existsByEmail("maria.wisniewska@test.pl")).thenReturn(true);
        //When + Then
        DoctorAlreadyExistsException exception = Assertions.assertThrows(DoctorAlreadyExistsException.class,
                () -> doctorService.create(createDoctorCommand));
        assertTrue(exception.getMessage().contains("Doctor already exists"));
    }

    @Test
    void updateDoctor_WhenDoctorExists_ShouldReturnUpdatedDoctorDto() {
        //Given
        Long existingId = 1L;
        PatchUserCommand userCommand = new PatchUserCommand(
                "changedName",
                "changedSurname",
                "newemai@example.com");
        PatchDoctorCommand patchDoctorCommand = new PatchDoctorCommand(
                "newMedicalSpeciality",
                userCommand);
        when(repository.findById(existingId)).thenReturn(Optional.of(doctorList.getFirst()));
        //When
        DoctorDto result = doctorService.update(existingId, patchDoctorCommand);
        //Then
        Assertions.assertAll(
                () -> assertEquals("newMedicalSpeciality", result.medicalSpecialty()),
                () -> assertEquals("changedName",result.userDto().firstName()),
                () -> assertEquals("changedSurname",result.userDto().lastName()),
                () -> assertEquals("newemai@example.com",result.userDto().email())
        );
    }


    @Test
    void updateDoctor_WhenDoctorDoesNotExist_ShouldThrowDoctorNotFoundException() {
        //Given
        Long id = 666L;
        PatchDoctorCommand patchDoctorCommand = new PatchDoctorCommand(
                "newMedicalSpeciality",
                null);
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        DoctorNotFoundException exception = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.update(id,patchDoctorCommand));
        assertTrue(exception.getMessage().contains("Doctor "+ id +" not found"));
    }

    @Test
    void deleteById_WhenDoctorExists_ShouldDeleteDoctor() {
        //Given
        Long existingId = 1L;
        when(repository.findById(existingId)).thenReturn(Optional.of(doctorList.getFirst()));
        //When + Then
        doctorService.delete(existingId);
        verify(repository).delete(doctorList.getFirst());
    }

    @Test
    void deleteById_WhenDoctorDoesNotExist_ShouldThrowDoctorNotFoundException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(DoctorNotFoundException.class,
                () -> doctorService.delete(id));
    }
}