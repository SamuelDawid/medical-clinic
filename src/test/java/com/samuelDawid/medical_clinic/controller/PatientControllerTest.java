package com.samuelDawid.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.exceptions.PatientNotFoundException;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    PatientService patientService;
    List<PatientDto> patientDtoList = TestDataFactory.threePatientDto();

    @Test
    void findAll_whenThreePatientsExists_ShouldReturnPageWithThreeElementsAnd200() throws Exception {
        //Given
        PageDto<PatientDto> page = new PageDto<>(patientDtoList, 0, 20, 3, 1);
        when(patientService.findAll(any(Pageable.class))).thenReturn(page);
        //When + Then
        mockMvc.perform(get("/patients"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(3)),
                        jsonPath("$.pageNumber").value(0),
                        jsonPath("$.pageSize").value(20),
                        jsonPath("$.totalElements").value(3),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.content[0].id").value(1)
                );
    }

    @Test
    void findById_WhenPatientExists_ShouldReturn200() throws Exception {
        //Given
        Long id = 2L;
        PatientDto patientDto = patientDtoList.get(1);
        when(patientService.findById(id)).thenReturn(patientDto);
        //When + Then
        mockMvc.perform(get("/patients/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.phoneNumber").value("222-333-444"),
                        jsonPath("$.birthDay").value("2005-05-15"),
                        jsonPath("$.id").value(2)
                );
    }

    @Test
    void findById_WhenPatientDoesNotExist_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        when(patientService.findById(id)).thenThrow(new PatientNotFoundException());
        //When + Then
        mockMvc.perform(get("/patients/{id}", id)).andExpectAll(
                status().isNotFound(),
                jsonPath("$.message").value("Patient not found"),
                jsonPath("$.status").value(404)
        );
    }

    @Test
    void create_WhenDetailsAreValid_ShouldReturn201() throws Exception {
        //Given
        CreateUserCommand userCommand = new CreateUserCommand(
                "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1"
        );
        CreatePatientCommand command = new CreatePatientCommand("ABC111111",
                LocalDate.of(2000, 1, 11),
                "111-222-33",
                userCommand);
        PatientDto patientDto = patientDtoList.getFirst();
        when(patientService.create(command)).thenReturn(patientDto);
        //When + Then
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.birthDay").value("2000-01-11"),
                        jsonPath("$.phoneNumber").value("111-222-333")
                );
    }

    @Test
    void update_WhenPatientExists_ShouldReturn200() throws Exception {
        //Given
        Long id = 1L;
        PatchPatientCommand command = new PatchPatientCommand(
                "newNumber",
                LocalDate.of(2020, 1, 1),
                "newPhoneNumber",
                null
        );
        PatientDto patientDto = new PatientDto(1L,
                null,
                LocalDate.of(2020, 1, 1),
                "newPhoneNumber");
        when(patientService.updatePatient(id, command)).thenReturn(patientDto);
        //When + Then
        mockMvc.perform(patch("/patients/{id}", id)
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.phoneNumber").value("newPhoneNumber"),
                        jsonPath("$.birthDay").value("2020-01-01")
                );
    }

    @Test
    void update_WhenPatientDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        PatchPatientCommand command = new PatchPatientCommand(
                "newNumber",
                LocalDate.of(2020, 1, 1),
                "newPhoneNumber",
                null
        );
        when(patientService.updatePatient(id, command)).thenThrow(new PatientNotFoundException());
        //When + Then
        mockMvc.perform(patch("/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Patient not found")
                );
    }

    @Test
    void delete_WhenPatientExists_ShouldReturn204() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/patients//id/{id}", id)).andExpect(status().isNoContent());
        verify(patientService).deleteById(id);
    }

    @Test
    void delete_WhenPatientDoesNotExists_ShouldReturn404() throws Exception {
        Long id = 666L;
        doThrow(new PatientNotFoundException()).when(patientService).deleteById(id);
        mockMvc.perform(delete("/patients//id/{id}", id)).andExpect(status().isNotFound());
        verify(patientService).deleteById(id);
    }
}