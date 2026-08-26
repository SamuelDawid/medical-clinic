package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.ChangePasswordCommand;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.exceptions.InvalidPasswordException;
import com.samuelDawid.medical_clinic.exceptions.UserAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.UserNotFoundException;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.TestDataFactory;
import com.samuelDawid.medical_clinic.model.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private EmailValidator emailValidator;

    private List<User> userList;

    @BeforeEach
    void setUp() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.emailValidator = new EmailValidator();
        this.userList = TestDataFactory.threeUsers();
        this.userService = new UserService(userRepository, userMapper, emailValidator);
    }

    @Test
    void findAll_WhenThreeUsersExist_ShouldReturnPageWithAllThree() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList, pageable, 3);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        //When
        PageDto<UserDto> result = userService.findAll(pageable);
        //Then
        UserDto first = result.content().getLast();
        Assertions.assertAll(
                () -> assertNotNull(result.content()),
                () -> assertEquals(3, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(3L, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals(10, result.pageSize()),
                () -> assertEquals("Maria", first.firstName()),
                () -> assertEquals("Wisniewska", first.lastName()),
                () -> assertEquals("maria.wisniewska@test.pl", first.email()),
                () -> assertEquals(3L, first.id())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findAll_WhenNoUsersExist_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(), pageable, 0);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        //When
        PageDto<UserDto> result = userService.findAll(pageable);
        //Then
        Assertions.assertAll(
                () -> assertTrue(result.content().isEmpty()),
                () -> assertEquals(0L, result.totalElements()),
                () -> assertEquals(0, result.totalPages()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(10, result.pageSize())
        );
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findById_WhenUserExists_ShouldReturnMatchingUserDto() {
        //Given
        Long existingId = 1L;
        when(userRepository.findById(existingId)).thenReturn(Optional.of(userList.getFirst()));
        //When
        UserDto userDto = userService.findById(existingId);
        //Then
        Assertions.assertAll(
                () -> assertEquals(existingId, userDto.id()),
                () -> assertEquals("Anna", userDto.firstName()),
                () -> assertEquals("Kowalska", userDto.lastName()),
                () -> assertEquals("anna.kowalska@test.pl", userDto.email())
        );
        verify(userRepository).findById(existingId);
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        //Given
        Long notExistingId = 99L;
        when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());
        //When + Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findById(notExistingId));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void create_WhenEmailIsValid_ShouldSaveAndReturnUserDto() {
        //Given
        CreateUserCommand command = new CreateUserCommand("Anna",
                "Kowalska",
                "       anna.kOwalskA@test.PL       ",
                "testUser1");
        when(userRepository.save(any(User.class))).thenReturn(userList.getFirst());
        //When
        UserDto userDto = userService.create(command);
        //Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User createdUser = captor.getValue();
        Assertions.assertAll(
                () -> assertEquals("Anna", userDto.firstName()),
                () -> assertEquals("Kowalska", userDto.lastName()),
                () -> assertEquals("anna.kowalska@test.pl", userDto.email()),
                () -> assertEquals("Anna", createdUser.getFirstName()),
                () -> assertEquals("Kowalska", createdUser.getLastName()),
                () -> assertEquals("anna.kowalska@test.pl", createdUser.getEmail())
        );
    }

    @Test
    void create_WhenEmailFormatIsInvalid_ShouldThrowInvalidEmailException() {
        //Given
        CreateUserCommand command = new CreateUserCommand("Anna",
                "Kowalska",
                "       anna.kOwalskA@@test.PL       ",
                "testUser1");
        when(userRepository.save(any(User.class))).thenReturn(userList.getFirst());
        //when + then
        InvalidEmailException exception = Assertions.assertThrows(
                InvalidEmailException.class, () -> userService.create(command));
        assertTrue(exception.getMessage().contains("Invalid email: "));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_WhenEmailAlreadyTaken_ShouldThrowUserAlreadyExistsException() {
        //Given
        CreateUserCommand createUserCommand = new CreateUserCommand("Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        when(userRepository.existsByEmail("maria.wisniewska@test.pl")).thenReturn(true);
        //when + Then
        UserAlreadyExistsException exception = Assertions.assertThrows(
                UserAlreadyExistsException.class, () -> userService.create(createUserCommand));
        assertTrue(exception.getMessage().contains("User already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_WhenUserExists_ShouldChangeUserPassword() {
        //Given
        User testPassword = TestDataFactory.buildUser(1L, "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword123");
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(testPassword));
        //When
        userService.changePassword(id, changePasswordCommand);
        //Then
        verify(userRepository).findById(id);
        assertEquals("newPassword123", testPassword.getPassword());
    }

    @Test
    void changePassword_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        //Given
        Long id = 99L;
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("newPassword123");
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(id, changePasswordCommand));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void changePassword_WhenPasswordIsTooShort_ShouldThrowInvalidPasswordException() {
        //Given
        Long id = 1L;
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand("asd");
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> userService.changePassword(id, changePasswordCommand));
        assertTrue(exception.getMessage().contains("Password does not meet criteria"));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        //Given
        Long id = 1L;
        User existingUser = userList.getFirst();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        //When
        userService.deleteUser(id);
        //Then
        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        //Given
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(id));
        assertTrue(exception.getMessage().contains("User not found"));
    }
}