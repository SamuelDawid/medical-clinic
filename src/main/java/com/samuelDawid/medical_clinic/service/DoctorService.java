package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.exceptions.InvalidEmailException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
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
public class DoctorService {
    private final DoctorRepository repository;
    private final UserRepository userRepository;
    private final DoctorMapper mapper;
    private final UserMapper userMapper;
    private final UserPatcher userPatcher;
    private final AffiliationService affiliationService;
    private final EmailValidator emailValidator;

    @Transactional(readOnly = true)
    public PageDto<DoctorDto> findAll(Pageable pageable) {
        return PageDto.from(repository.findAll(pageable)
                .map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public DoctorDto findById(@NonNull Long id) {
        return mapper.toDto(getDoctorOrThrow(id));
    }

    @Transactional
    public DoctorDto create(@NonNull CreateDoctorCommand command) {
        log.info("Creating new Doctor");
        String email = EmailValidator.normalize(command.user()
                .email());
        validateEmail(email);

        Doctor doctor = buildDoctor(command, email);
        affiliationService.assignInstitutionsByIdToDoctor(command.institutionId(), doctor);

        repository.save(doctor);
        log.info("Doctor {} created successfully", doctor.getId());
        return mapper.toDto(doctor);
    }

    @Transactional
    public DoctorDto update(@NonNull Long id, @NonNull PatchDoctorCommand command) {
        log.info("Updating Doctor {}", id);
        Doctor doctor = getDoctorOrThrow(id);
        doctor.update(command, userPatcher);
        log.info("Doctor {} updated successfully", id);
        return mapper.toDto(doctor);
    }

    @Transactional
    public void delete(@NonNull Long id) {
        log.info("Removing Doctor {}", id);
        Doctor doctor = getDoctorOrThrow(id);
        repository.delete(doctor);
        log.info("Doctor {} removed successfully", id);
    }

    private Doctor buildDoctor(@NonNull CreateDoctorCommand command, String normalizedEmail) {
        Doctor doctor = mapper.toEntity(command);
        User user = userMapper.toEntity(command.user());
        user.setEmail(normalizedEmail);
        doctor.setUser(user);
        return doctor;
    }

    private void validateEmail(String email) {
        if (!emailValidator.validate(email)) {
            log.warn("email : {} not a valid email",email);
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("user with email {} already exists",email);
            throw new DoctorAlreadyExistsException();
        }
    }
    private Doctor getDoctorOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor with id {} does not exists", id);
                    return new DoctorNotFoundException(id);
                });
    }
}
