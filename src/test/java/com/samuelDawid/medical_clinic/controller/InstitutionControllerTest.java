package com.samuelDawid.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuelDawid.medical_clinic.dto.CreateAddressCommand;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorSummaryDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInstitutionCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InstitutionNotFoundException;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.service.InstitutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InstitutionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    InstitutionService institutionService;
    List<InstitutionDto> institutionDtoList = TestDataFactory.threeInstitutionsDto();
    Set<DoctorSummaryDto> doctorSummaries = Set.of(
            new DoctorSummaryDto("Anna", "Kowalska", "Kardiologia"),
            new DoctorSummaryDto("Piotr", "Nowak", "Ortopedia")
    );

    @Test
    void findAll_WhenThreeInstitutionsExists_ShouldReturn200() throws Exception {
        //Given
        PageDto<InstitutionDto> pageDto = new PageDto<>(institutionDtoList, 0, 20, 3, 1);
        when(institutionService.findAll(any(Pageable.class))).thenReturn(pageDto);
        //When + Then
        mockMvc.perform(get("/institutions"))
                .andDo(print())
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
    void findById_WhenInstitutionExists_ShouldReturn200() throws Exception {
        Long id = 1L;
        InstitutionDto institution = institutionDtoList.getFirst();
        when(institutionService.findById(id)).thenReturn(institution);
        mockMvc.perform(get("/institutions/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Szpital Kliniczny im. Jana Pawła II"),
                        jsonPath("$.address.city").value("Warszawa"),
                        jsonPath("$.address.postCode").value("00-001")
                );
        verify(institutionService).findById(1L);
    }

    @Test
    void findById_WhenInstitutionDoesNotExists_ShouldReturn404() throws Exception {
        Long id = 666L;
        doThrow(new InstitutionNotFoundException(id)).when(institutionService).findById(id);
        //When + Then
        mockMvc.perform(get("/institutions/{id}", id)).andExpectAll(
                status().isNotFound(),
                jsonPath("$.message").value("Institution 666 NotFound"),
                jsonPath("$.status").value(404)
        );
    }

    @Test
    void findDoctorsByInstitutionName_WhenInstitutionIsValidAndDoctorExists_ShouldReturn200() throws Exception {
        //Given
        Long id = 1L;
        Set<DoctorDto> response = Set.of(TestDataFactory.threeDoctorsDto().getFirst(), TestDataFactory.threeDoctorsDto().get(1));
        when(institutionService.showDoctors(id)).thenReturn(response);
        //When + Then
        mockMvc.perform(get("/institutions/{id}/doctors", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2))
                );
    }

    @Test
    void addDoctorsToInstitution_WhenDoctorsExist_ShouldReturnStatus200() throws Exception {
        //Given
        Long institutionId = 1L;
        Set<Long> doctorsId = Set.of(1L, 2L);
        InstitutionDoctorsDto expected = new InstitutionDoctorsDto("Centrum Medyczne Alfa", doctorSummaries);
        when(institutionService.addDoctorsToInstitution(doctorsId, institutionId)).thenReturn(expected);
        //When + Then
        mockMvc.perform(patch("/institutions/institution/{id}/doctors", institutionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorsId)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.institutionName").value("Centrum Medyczne Alfa"),
                        jsonPath("$.doctorsDto", hasSize(doctorSummaries.size())
                        ));
        verify(institutionService).addDoctorsToInstitution(doctorsId, 1L);
    }

    @Test
    void addDoctorsToInstitution_WhenInstitutionNotFound_ShouldReturnStatus404() throws Exception {
        Long id = 666L;
        Set<Long> doctorsId = Set.of(1L, 2L);
        when(institutionService.addDoctorsToInstitution(anySet(), eq(id)))
                .thenThrow(new InstitutionNotFoundException(id));

        mockMvc.perform(patch("/institutions/institution/{id}/doctors", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorsId)))
                .andExpect(status().isNotFound());

        verify(institutionService).addDoctorsToInstitution(doctorsId, id);
    }

    @Test
    void addDoctorsToInstitution_WhenDoctorNotFound_ShouldReturnStatus404() throws Exception {
        Long institutionId = 1L;
        Long missingDoctorId = 404L;
        Set<Long> doctorsId = Set.of(1L, missingDoctorId);
        when(institutionService.addDoctorsToInstitution(anySet(), eq(institutionId)))
                .thenThrow(new DoctorNotFoundException(missingDoctorId));

        mockMvc.perform(patch("/institutions/institution/{id}/doctors", institutionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorsId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WhenCommandIsValid_ShouldReturnStatus201() throws Exception {
        CreateAddressCommand addressCommand = new CreateAddressCommand(
                "Warszawa",
                "00-001",
                "Marszalkowska",
                "12A"
        );
        CreateInstitutionCommand command = new CreateInstitutionCommand(
                "Szpital Kliniczny im. Jana Pawła II",
                addressCommand
        );
        InstitutionDto expected = institutionDtoList.getFirst();
        when(institutionService.create(any(CreateInstitutionCommand.class))).thenReturn(expected);
        mockMvc.perform(post("/institutions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(expected.id()),
                        jsonPath("$.name").value(expected.name()),
                        jsonPath("$.address.city").value("Warszawa")
                );
    }
    @Test
    void  update_WhenInstitutionExists_ShouldReturnStatus200() throws Exception {
        //Given
        Long id = 1L;
        PatchInstitutionCommand command = new PatchInstitutionCommand(
                "Szpital Kliniczny im. Jana Pawła II",
                "Warszawa",
                "00-001",
                "Marszalkowska",
                "12A");
        InstitutionDto expected = institutionDtoList.getFirst();
        when(institutionService.update(id,command)).thenReturn(expected);
        mockMvc.perform(patch( "/institutions/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(expected.id()),
                        jsonPath("$.name").value(expected.name()),
                        jsonPath("$.address.city").value("Warszawa")
                );
        verify(institutionService).update(id, command);
    }
    @Test
    void update_WhenInstitutionNotFound_ShouldReturnStatus404() throws Exception {
        Long id = 666L;
        PatchInstitutionCommand command = new PatchInstitutionCommand(
                "Nowa Nazwa",
                "Krakow",
                "30-002",
                "Florianska",
                "45");
        when(institutionService.update(id,command)).thenThrow(new InstitutionNotFoundException(id));

        mockMvc.perform(patch("/institutions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());

        verify(institutionService).update(id, command);
    }
    @Test
    void delete_WhenInstitutionExists_ShouldReturnStatus204() throws Exception {
        Long id = 1L;
        doNothing().when(institutionService).delete(id);

        mockMvc.perform(delete("/institutions/{id}", id))
                .andExpect(status().isNoContent());

        verify(institutionService).delete(id);
    }
    @Test
    void delete_WhenInstitutionNotFound_ShouldReturnStatus404() throws Exception {
        Long id = 666L;
        doThrow(new InstitutionNotFoundException(id)).when(institutionService).delete(id);

        mockMvc.perform(delete("/institutions/{id}", id))
                .andExpect(status().isNotFound());

        verify(institutionService).delete(id);
    }

}