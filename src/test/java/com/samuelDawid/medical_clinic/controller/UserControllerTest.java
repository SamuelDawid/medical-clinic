package com.samuelDawid.medical_clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuelDawid.medical_clinic.dto.ChangePasswordCommand;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidPasswordException;
import com.samuelDawid.medical_clinic.exceptions.UserNotFoundException;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService userService;
    List<UserDto> threeUsers = TestDataFactory.threeUsersDto;

    @Test
    void findAll_WhenUsersExists_ShouldReturnPageWithThreeElementsAnd200() throws Exception {
        //Given
        PageDto<UserDto> pageDto = new PageDto<>(threeUsers, 0, 20, 3, 1);
        when(userService.findAll(any(Pageable.class))).thenReturn(pageDto);
        //When + Then
        mockMvc.perform(get("/users"))
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
    void findById_WhenUserExists_ShouldReturn200() throws Exception {
        //Given
        Long id = 1L;
        UserDto userDto = threeUsers.getFirst();
        when(userService.findById(id)).thenReturn(userDto);
        //When + Then
        mockMvc.perform(get("/users/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.firstName").value("Anna"),
                        jsonPath("$.lastName").value("Kowalska"),
                        jsonPath("$.email").value("anna.kowalska@test.pl")
                );
    }

    @Test
    void findById_WhenUserDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        when(userService.findById(id)).thenThrow(new UserNotFoundException());
        //When + Then
        mockMvc.perform(get("/users/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("User not found"),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    void create_WhenUserDataIsValid_ShouldReturn204() throws Exception {
        //Given
        CreateUserCommand command = new CreateUserCommand(
                "Anna",
                "Kowalska",
                "example@example.com",
                "password123"
        );
        UserDto dto = new UserDto(1L,
                "Anna",
                "Kowalska",
                "example@example.com"
        );
        when(userService.create(command)).thenReturn(dto);
        //When + Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.firstName").value("Anna"),
                        jsonPath("$.lastName").value("Kowalska"),
                        jsonPath("$.email").value("example@example.com")
                );
    }

    @Test
    void create_whenPasswordIsInvalid_ShouldReturn400() throws Exception {
        //Given
        CreateUserCommand command = new CreateUserCommand(
                "Anna",
                "Kowalska",
                "example@example.com",
                "pas"
        );
        when(userService.create(command)).thenThrow(new InvalidPasswordException());
        //When + Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value(400),
                        jsonPath("$.message").value("Password does not meet criteria")
                );
    }

    @Test
    void create_whenUserIsNotFound_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        CreateUserCommand command = new CreateUserCommand(
                "Anna",
                "Kowalska",
                "example@example.com",
                "pas"
        );
        when(userService.create(command)).thenThrow(new UserNotFoundException());
        //When + Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("User not found"),
                        jsonPath("$.status").value(404)
                );
    }

    @Test
    void changePassword_WhenUserIsFoundAndNewPasswordIsValid_ShouldReturn204() throws Exception {
        //Given
        Long id = 1L;
        ChangePasswordCommand command = new ChangePasswordCommand("newPassword123");
        //When + Then
        mockMvc.perform(patch("/users/{id}/password", id)
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        verify(userService).changePassword(id, command);
    }

    @Test
    void changePassword_WhenUserIsNotFound_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        ChangePasswordCommand command = new ChangePasswordCommand("newPassword123");
        //When + Then
        doThrow(new UserNotFoundException()).when(userService).changePassword(id, command);
        mockMvc.perform(patch("/users/{id}/password", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
        verify(userService).changePassword(id, command);
    }

    @Test
    void delete_WhenUserExistsShouldReturn204() throws Exception {
        //Given
        Long id = 1L;
        //When + Then
        mockMvc.perform(delete("/users/{id}", id)).andExpect(status().isNoContent());
        verify(userService).deleteUser(id);
    }

    @Test
    void delete_WhenUserDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        doThrow(new UserNotFoundException()).when(userService).deleteUser(id);
        //When + Then
        mockMvc.perform(delete("/users/{id}", id)).andExpect(status().isNotFound());
        verify(userService).deleteUser(id);
    }
}