package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.CreateAddressCommand;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorSummaryDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.mappers.InstitutionMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.model.institution.Address;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.repository.InstitutionRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InstitutionServiceTest {
    private InstitutionService service;
    private InstitutionRepository repository;
    private DoctorRepository doctorRepository;
    private InstitutionMapper mapper;
    private DoctorMapper doctorMapper;
    private AffiliationService affiliationService;
    List<Address> addressList;
    List<Institution> institutionList;
    List<Doctor> doctorList;

    @BeforeEach
    void setUp() {
        this.repository = Mockito.mock(InstitutionRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.mapper = Mappers.getMapper(InstitutionMapper.class);
        this.affiliationService = new AffiliationService(doctorRepository, repository, doctorMapper);
        this.service = new InstitutionService(repository, mapper, affiliationService);
        this.institutionList = TestDataFactory.threeInstitutions();
        this.doctorList = TestDataFactory.threeDoctors();
        this.addressList = TestDataFactory.threeAddresses();
    }

    @Test
    void findAll_whenThreeInstitutionsExist_ShouldReturnPageWithAllThree() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Institution> page = new PageImpl<>(institutionList, pageable, 3);
        when(repository.findAll(pageable)).thenReturn(page);
        PageDto<InstitutionDto> result = service.findAll(pageable);
        InstitutionDto first = result.content().getFirst();
        Assertions.assertAll(
                () -> assertNotNull(result.content()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(10, result.pageSize()),
                () -> assertEquals(3L, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals("Centrum Medyczne Alfa", first.name()),
                () -> assertEquals(1L, first.id()),
                () -> assertEquals("Warszawa", first.address().getCity()),
                () -> assertEquals("00-001", first.address().getPostCode()),
                () -> assertEquals("Marszalkowska", first.address().getStreet()),
                () -> assertEquals("12A", first.address().getBuildingNumber())
        );
        verify(repository).findAll(pageable);
    }

    @Test
    void findAll_whenNoInstitutionExists_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Institution> page = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(page);
        //When
        PageDto<InstitutionDto> result = service.findAll(pageable);
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
    void findById_WhenInstitutionExists_ShouldReturnMatchingInstitutionDto() {
        //Given
        Long existingId = 1L;
        Institution existing = institutionList.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(existing));
        //When
        InstitutionDto result = service.findById(existingId);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Centrum Medyczne Alfa", result.name()),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Warszawa", result.address().getCity()),
                () -> assertEquals("00-001", result.address().getPostCode()),
                () -> assertEquals("Marszalkowska", result.address().getStreet()),
                () -> assertEquals("12A", result.address().getBuildingNumber())
        );
        verify(repository).findById(existingId);
    }

    @Test
    void findById_WhenInstitutionDoesNotExist_ShouldThrowInstitutionNotFoundException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        InstitutionNotFoundException exception = assertThrows(InstitutionNotFoundException.class,
                () -> service.findById(id));
        assertTrue(exception.getMessage().contains("Institution 666 NotFound"));
    }

    @Test
    void create_WhenInstitutionIsValid_ShouldSaveAndReturnInstitutionDto() {
        //Given
        CreateAddressCommand addressCommand = new CreateAddressCommand(
                "Warszawa",
                "00-001",
                "Marszalkowska",
                "12A"
        );
        CreateInstitutionCommand command = new CreateInstitutionCommand(
                "Centrum Medyczne Alfa",
                addressCommand);
        when(repository.save(any(Institution.class))).thenReturn(institutionList.getFirst());
        //When
        InstitutionDto result = service.create(command);
        //Then
        ArgumentCaptor<Institution> captor = ArgumentCaptor.forClass(Institution.class);
        verify(repository).save(captor.capture());
        Assertions.assertAll(
                () -> assertEquals("Warszawa", result.address().getCity()),
                () -> assertEquals("12A", result.address().getBuildingNumber()),
                () -> assertEquals("Marszalkowska", result.address().getStreet()),
                () -> assertEquals("00-001", result.address().getPostCode()),
                () -> assertEquals("Centrum Medyczne Alfa", result.name())
        );
    }

    @Test
    void create_WhenInstitutionWithThisNameAlreadyExists_ShouldThrowInstitutionAlreadyExistsException() {
        //Given
        CreateInstitutionCommand command = new CreateInstitutionCommand("Centrum Medyczne Alfa", null);
        when(repository.existsByName("Centrum Medyczne Alfa")).thenReturn(true);
        //When + Then
        InstitutionAlreadyExistsException existsException = assertThrows(InstitutionAlreadyExistsException.class,
                () -> service.create(command));
        assertTrue(existsException.getMessage().contains("Institution Already Exists"));
    }

    @Test
    void addDoctorsToInstitution_WhenInstitutionExits_ShouldSaveAndReturnInstitutionDoctorsDto() {
        //Given
        Set<Long> doctorsIdList = Set.of(1L, 2L);
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(institutionList.getFirst()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctorList.getFirst()));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctorList.get(1)));
        //When
        InstitutionDoctorsDto result = service.addDoctorsToInstitution(doctorsIdList, id);
        //Then
        List<DoctorSummaryDto> doctorSummaryDtoList = result.doctorsDto().stream().toList();
        DoctorSummaryDto first = doctorSummaryDtoList.getFirst();
        DoctorSummaryDto second = doctorSummaryDtoList.getLast();
        Assertions.assertAll(
                () -> assertEquals("Centrum Medyczne Alfa", result.institutionName()),
                () -> assertFalse(result.doctorsDto().isEmpty()),
                () -> assertEquals("Anna", first.firstName()),
                () -> assertEquals("Kowalska", first.lastName()),
                () -> assertEquals("SpecialityOne", first.medicalSpecialty()),
                () -> assertEquals("Piotr", second.firstName()),
                () -> assertEquals("Nowak", second.lastName()),
                () -> assertEquals("SpecialityTwo", second.medicalSpecialty())
        );
    }

    @Test
    void addDoctorsToInstitution_WhenDoctorsIdIsInvalid_ShouldThrowDoctorNotFoundException() {
        //Given
        Set<Long> doctorsIdList = Set.of(1L, 2L);
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(institutionList.getFirst()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctorList.get(1)));
        //When + Then
        assertThrows(DoctorNotFoundException.class,
                () -> service.addDoctorsToInstitution(doctorsIdList, id));
    }

    @Test
    void addDoctorsToInstitution_WhenInstitutionIdIsInvalid_ShouldThrowInstitutionNotFoundException() {
        Long id = 666L;
        Set<Long> doctorsIdList = Set.of(1L, 2L);
        when(repository.findById(id)).thenReturn(Optional.empty());
        InstitutionNotFoundException exception = Assertions.assertThrows(InstitutionNotFoundException.class,
                () -> service.addDoctorsToInstitution(doctorsIdList, id));
        assertTrue(exception.getMessage().contains("Institution " + id + " NotFound"));
    }

    @Test
    void showDoctors_WhenDoctorsArePresent_ShouldReturnMatchingDoctorDto() {
        Long existingId = 1L;
        when(doctorRepository.findByInstitutionsId(existingId)).thenReturn(Set.of(doctorList.getFirst()));
        //When
        List<DoctorDto> result = service.showDoctors(existingId).stream().toList();
        DoctorDto first = result.getFirst();
        //Then
        Assertions.assertAll(
                () -> assertEquals("Anna", first.userDto().firstName()),
                () -> assertEquals("Kowalska", first.userDto().lastName()),
                () -> assertEquals("anna.kowalska@test.pl", first.userDto().email()),
                () -> assertEquals("SpecialityOne", first.medicalSpecialty()),
                () -> assertEquals(1L, first.id())
        );
    }

    @Test
    void update_WhenInstitutionExists_ShouldSaveAndReturnInstitutionDto() {
        Long existingId = 1L;
        PatchInsitutionCommand command = new PatchInsitutionCommand(
                "newName",
                "newCity",
                "newPostCode01",
                "new street",
                "new15A"
        );
        when(repository.findById(existingId)).thenReturn(Optional.of(institutionList.getFirst()));
        //When
        InstitutionDto result = service.update(existingId, command);
        //Then
        Assertions.assertAll(
                () -> assertEquals("newName", result.name()),
                () -> assertEquals("newCity", result.address().getCity()),
                () -> assertEquals("newPostCode01", result.address().getPostCode()),
                () -> assertEquals("new street", result.address().getStreet()),
                () -> assertEquals("new15A", result.address().getBuildingNumber()),
                () -> assertEquals(1L, result.id())
        );
    }

    @Test
    void update_WhenInstitutionDoesNotExists_ShouldThrowInstitutionNotFoundException() {
        Long id = 666L;
        PatchInsitutionCommand command = new PatchInsitutionCommand(null, null, null, null, null);
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(InstitutionNotFoundException.class,
                () -> service.update(id, command));
    }

    @Test
    void delete_WhenInstitutionExists_ShouldRemoveInstitution() {
        Long existingId = 1L;
        when(repository.findById(existingId)).thenReturn(Optional.of(institutionList.getFirst()));
        service.delete(existingId);
        verify(repository).delete(any(Institution.class));
    }

}