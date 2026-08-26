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
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.UserRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailValidator emailValidator;

    @Transactional(readOnly = true)
    public PageDto<UserDto> findAll(Pageable pageable) {
        return PageDto.from(userRepository.findAll(pageable)
                .map(userMapper::toDto));
    }

    @Transactional
    public UserDto create(@NonNull CreateUserCommand command) {
        log.info("Creating new user");
        String emailNormalized = EmailValidator.normalize(command.email());
        validateEmail(emailNormalized);
        validatePassword(command.password());
        User user = userMapper.toEntity(command);
        user.setEmail(emailNormalized);
        userRepository.save(user);
        log.info("User with Id {} created successfully", user.getId());
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto findById(@NonNull Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id : {} not found",id);
                    return new UserNotFoundException();
                });
        return userMapper.toDto(user);
    }

    @Transactional
    public void changePassword(@NonNull Long id, @NonNull ChangePasswordCommand command) {
        log.info("Changing password for User with Id {}", id);
        validatePassword(command.newPassword());
        User user = getUserOrThrow(id);
        user.setPassword(command.newPassword());
        log.info("Password changed for User {}", id);
    }

    @Transactional
    public void deleteUser(@NonNull Long id) {
        log.info("Deleting user with Id {}", id);
        User user = getUserOrThrow(id);
        userRepository.delete(user);
        log.info("User with Id {} removed", id);
    }

    private void validateEmail(String email) {
        if (!emailValidator.validate(email)) {
            log.warn("email : {} not a valid email",email);
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("user with email {} already exists",email);
            throw new UserAlreadyExistsException();
        }
    }

    private void validatePassword(String password) {
        if (password.isBlank()) {
            log.warn("blank or null password");
            throw new InvalidPasswordException();
        }
        if(password.length() < 5){
            log.warn("password too short");
            throw new InvalidPasswordException();
        }
    }
    private User getUserOrThrow(Long id){
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("deleting user with id {} failed, user not found",id);
            return new UserNotFoundException();
        });
    }
}
