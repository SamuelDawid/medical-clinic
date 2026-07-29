package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.ChangePasswordCommand;
import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.exceptions.InvalidPasswordException;
import com.samuelDawid.medical_clinic.exceptions.UserAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.UserNotFoundException;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto create(@NonNull CreateUserCommand command) {
        if (userRepository.findAll().stream().map(User::getEmail).toList().contains(command.email())) {
            throw new UserAlreadyExistsException();
        }
        if (command.password().isBlank()) {
            throw new InvalidPasswordException();
        }
        User user = userMapper.toEntity(command);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto findById(@NonNull Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public void changePassword(@NonNull Long id, @NonNull ChangePasswordCommand command) {
        if (command.newPassword().isBlank()) {
            throw new InvalidPasswordException();
        }
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setPassword(command.newPassword());
        userRepository.save(user);
    }

    public void deleteUser(@NonNull Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }
}
