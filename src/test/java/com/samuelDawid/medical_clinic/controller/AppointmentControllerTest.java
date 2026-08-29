package com.samuelDawid.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.AssignPatientToAppointmentCommand;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    AppointmentService appointmentService;
    List<AppointmentDto> appointmentList = TestDataFactory.threeAppointmentsDtos();
    @Autowired
    private ServerProperties serverProperties;

    @Test
    void findAll_WhenAppointmentsExists_ShouldReturnPageWithThreeAppointments() throws Exception {
        //Given
        PageDto<AppointmentDto> page = new PageDto<>(appointmentList, 0, 20, 3, 1);
        when(appointmentService.findAll(any(Pageable.class))).thenReturn(page);
        //When + Then
        mockMvc.perform(get("/appointments").param("page", "0").param("size", "20"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").value(hasSize(3)),
                        jsonPath("$.pageNumber").value(0),
                        jsonPath("$.pageSize").value(20),
                        jsonPath("$.totalElements").value(3),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[0].doctorName").value("Anna Kowalska")
                );
    }

    @Test
    void findById_WhenAppointmentExists_ShouldReturn200WithAppointmentDto() throws Exception {
        //Given
        Long existingId = 1L;
        AppointmentDto appointmentDto = appointmentList.getFirst();
        when(appointmentService.findById(existingId)).thenReturn(appointmentDto);
        //When + Then
        mockMvc.perform(get("/appointments/{id}", existingId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.startDateTime").value("2026-09-15T15:30:00"),
                        jsonPath("$.endDateTime").value("2026-09-15T16:15:00"),
                        jsonPath("$.doctorName").value("Anna Kowalska"),
                        jsonPath("$.patientName").value("Piotr Nowak")
                );
    }

    @Test
    void findById_WhenAppointmentDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        when(appointmentService.findById(id)).thenThrow(new AppointmentDoesNotExistsException());
        //When + Then
        mockMvc.perform(get("/appointments/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Appointment Does Not Exists"),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    void findAllByPatientId_WhenAppointmentsExists_ShouldReturnThreeAppointmentAnd200() throws Exception {
        //Given
        Long existingId = 1L;
        String name = "Anna Kowalska";
        PageDto<AppointmentDto> appointmentDtos = new PageDto<>(TestDataFactory.threeAppointmentsFotTheSamePatient(name), 0, 20, 3, 1);
        when(appointmentService.findAllByPatientId(eq(existingId), any(Pageable.class))).thenReturn(appointmentDtos);
        //When + Then
        mockMvc.perform(get("/appointments/patient/{id}", existingId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.pageSize").value(20),
                        jsonPath("$.totalElements").value(3),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.content[0].id").value(1),
                        jsonPath("$.content[0].startDateTime").value("2026-09-01T09:15:00"),
                        jsonPath("$.content[1].id").value(2),
                        jsonPath("$.content[2].id").value(3)
                );

    }

    @Test
    void create_WhenDoctorExists_ShouldCreateAndReturn201() throws Exception {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                null,
                1L,
                LocalDateTime.of(2026, 9, 3, 13, 30),
                LocalDateTime.of(2026, 9, 3, 14, 15)
        );
        AppointmentDto appointmentDto = new AppointmentDto(1L, LocalDateTime.of(2026, 9, 3, 13, 30),
                LocalDateTime.of(2026, 9, 3, 14, 15), "Piotr Nowak", null);
        when(appointmentService.create(command)).thenReturn(appointmentDto);
        //When + Then
        mockMvc.perform(
                        post("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.patientName").isEmpty(),
                        jsonPath("$.doctorName").value("Piotr Nowak")
                );
    }

    @Test
    void create_WhenAppointmentInThePast_ShouldReturn400() throws Exception {
        CreateAppointmentCommand wrongDate = new CreateAppointmentCommand(
                null,
                1L,
                LocalDateTime.of(2026, 1, 3, 13, 30),
                LocalDateTime.of(2026, 1, 3, 14, 15)
        );
        when(appointmentService.create(wrongDate)).thenThrow(new InvalidDateOfAppointmentException());
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongDate)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Date must be in the future"),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    void create_WhenAppointmentWhenTimeIsNotFullQuarter_ShouldReturn400() throws Exception {
        //Given
        CreateAppointmentCommand wrongTime = new CreateAppointmentCommand(
                null,
                1L,
                LocalDateTime.of(2026, 9, 3, 13, 12),
                LocalDateTime.of(2026, 9, 3, 14, 15)
        );
        when(appointmentService.create(wrongTime)).thenThrow(new InvalidTimeOfTheAppointmentException());
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongTime)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid Time Of The Appointment"),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    void create_WhenDoctorNotFound_ShouldReturn404() throws Exception {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                2L,
                1L,
                LocalDateTime.of(2026, 9, 3, 13, 30),
                LocalDateTime.of(2026, 9, 3, 14, 15)
        );
        when(appointmentService.create(command)).thenThrow(new DoctorNotFoundException(1L));
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Doctor 1 not found"),
                        jsonPath("$.status").value(404)
                );
    }
    @Test
    void create_WhenAppointmentOverlaps_ShouldReturn409() throws Exception {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                2L,
                1L,
                LocalDateTime.of(2026, 9, 3, 13, 30),
                LocalDateTime.of(2026, 9, 3, 14, 15)
        );
        when(appointmentService.create(command)).thenThrow(new TimeIsOverlappingWithAnotherAppointmentException());
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value("Another appointment at this time already exists"),
                        jsonPath("$.status").value(409)
                );
    }
    @Test
    void assignPatientToAppointment_WhenAppointmentAndPatientExists_ShouldReturn200() throws Exception{
        //Given
        Long existingPatientId = 1L;
        Long existingAppointmentId = 2L;
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(existingPatientId,existingAppointmentId);
        AppointmentDto result = new AppointmentDto(
                1L,
                LocalDateTime.of(2026, 9, 3, 13, 30),
                LocalDateTime.of(2026, 9, 3, 14, 15),
                "Anna Kowalska",
                "Piotr Nowak");
        when(appointmentService.assignPatientToAppointment(command)).thenReturn(result);
        //Then + When
        mockMvc.perform(put("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1)
                );
    }
    @Test
    void patientAppointmentCancel_WhenPatientAndAppointmentExists_ShouldReturn204() throws Exception{
        Long appointmentId = 1L;
        mockMvc.perform(patch("/appointments/cancel/{appointmentId}",appointmentId))
                .andExpect(status().isNoContent());
        verify(appointmentService).removePatientFromVisit(appointmentId);
    }
    @Test
    void patientAppointmentCancel_ShouldReturnNotFoundWhenCancellingNonExistingAppointmentAndReturn404() throws Exception {
        Long appointmentId = 666L;
        doThrow(new AppointmentDoesNotExistsException())
                .when(appointmentService)
                .removePatientFromVisit(appointmentId);
        mockMvc.perform(patch("/appointments/cancel/{appointmentId}",appointmentId))
                .andExpect(status().isNotFound());
        verify(appointmentService).removePatientFromVisit(appointmentId);
    }
    @Test
    void delete_whenAppointmentExists_ShouldDeleteAppointmentAndReturn204() throws Exception{
        Long appointmentId = 1L;

        mockMvc.perform(delete("/appointments/{id}", appointmentId))
                .andExpect(status().isNoContent());

        verify(appointmentService).delete(appointmentId);
    }
    @Test
    void delete_WhenAppointmentDoesNotExists_ShouldReturnAppointmentDoesNotExistsAnd404() throws Exception{
        Long appointmentId = 999L;
        doThrow(new AppointmentDoesNotExistsException())
                .when(appointmentService)
                .delete(appointmentId);
        mockMvc.perform(delete("/appointments/{id}",appointmentId))
                .andExpect(status().isNotFound());
        verify(appointmentService).delete(appointmentId);
    }
}