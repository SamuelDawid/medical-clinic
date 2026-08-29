package com.samuelDawid.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.PatchUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    DoctorService doctorService;
    List<DoctorDto> doctorDtoList = TestDataFactory.threeDoctorsDto();

    @Test
    void findAll_WhenDoctorsExists_ShouldReturnPageWithThreeDoctors() throws Exception {
        //Given
        PageDto<DoctorDto> page = new PageDto<>(doctorDtoList,0,20,3,1);
        when(doctorService.findAll(any(Pageable.class))).thenReturn(page);
        //When + Then
        mockMvc.perform(get("/doctors").param("page", "0").param("size", "20"))
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
    void findById_WhenDoctorExists_ShouldReturn200() throws Exception {
        //Given
        Long id = 1L;
        DoctorDto doctorDto = doctorDtoList.getFirst();
        when(doctorService.findById(id)).thenReturn(doctorDto);
        //When + Then
        mockMvc.perform(get("/doctors/{id}",id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.medicalSpecialty").value("Kardiologia"),
                        jsonPath("$.userDto.firstName").value("Anna")
                );
    }
    @Test
    void findById_WhenDoctorDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        when(doctorService.findById(id)).thenThrow(new DoctorNotFoundException(id));
        //When + Then
        mockMvc.perform(get("/doctors/{id}",id))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Doctor 666 not found"),
                        jsonPath("$.status").value(404)
                );
    }
    @Test
    void create_ShouldCreateDoctorWhenValid_AndReturn201() throws Exception {
        //Given
        CreateUserCommand userCommand = new CreateUserCommand(
                "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1"
        );
        CreateDoctorCommand command = new CreateDoctorCommand(
                "Kardiologia",
                userCommand,
                Set.of(1L));
        DoctorDto existing = doctorDtoList.getFirst();
        when(doctorService.create(command)).thenReturn(existing);
        //When + Then
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.medicalSpecialty").value("Kardiologia"),
                        jsonPath("$.userDto.firstName").value("Anna")
                );
    }
    @Test
    void create_WhenProvidedWithInvalidData_ShouldReturn400() throws Exception{
        //Given
        CreateDoctorCommand command = new CreateDoctorCommand(
                "Kardiologia",
                null,
                Set.of(1L));
        when(doctorService.create(command)).thenThrow(new InvalidEmailException(null));
        //When + Then
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid email: null"),
                        jsonPath("$.status").value(400)
                );
    }
    @Test
    void update_whenDoctorExists_ShouldReturn200() throws Exception{
        //Given
        Long id = 1L;
        PatchUserCommand patchUserCommand = new PatchUserCommand(
                "newName",
                "newSurname",
                "newEmail@example.com"
        );
        PatchDoctorCommand patchDoctorCommand = new PatchDoctorCommand(
                "newSpeciality", patchUserCommand
        );
        UserDto userDto = new UserDto(
                1L,
                "newName",
                "newSurname",
                "newEmail@example.com"
        );
        DoctorDto doctorDto = new DoctorDto(
                1L,
                "newSpeciality",
                null,
                userDto
        );
        when(doctorService.update(id,patchDoctorCommand)).thenReturn(doctorDto);
        //When + Then
        mockMvc.perform(patch("/doctors/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDoctorCommand)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.medicalSpecialty").value("newSpeciality"),
                        jsonPath("$.userDto.firstName").value("newName"),
                        jsonPath("$.userDto.lastName").value("newSurname"),
                        jsonPath("$.userDto.email").value("newEmail@example.com")
                );
    }
    @Test
    void update_WhenDoctorDoesNotExists_ShouldReturn404() throws Exception{
        //Given
        Long id = 666L;
        PatchDoctorCommand patchDoctorCommand = new PatchDoctorCommand(
                "newSpeciality", null
        );
        when(doctorService.update(id,patchDoctorCommand)).thenThrow(new DoctorNotFoundException(id));
        //When + Then
        mockMvc.perform(patch("/doctors/{id}",id)
                .content(objectMapper.writeValueAsString(patchDoctorCommand))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Doctor 666 not found"),
                        jsonPath("$.status").value(404)
                );
    }
    @Test
    void delete_whenDoctorExists_ShouldReturn204() throws Exception{
        //Given
        Long id = 1L;
        //When + Then
        mockMvc.perform(delete("/doctors/{id}",id)).andExpect(status().isNoContent());
        verify(doctorService).delete(id);
    }
    @Test
    void delete_WhenDoctorDoesNotExists_ShouldReturn404() throws Exception{
        //Given
        Long id = 666L;
        doThrow(new DoctorNotFoundException(id))
                .when(doctorService)
                .delete(id);
        mockMvc.perform(delete("/doctors/{id}",id))
                .andExpect(status().isNotFound());
        verify(doctorService).delete(id);
    }
}