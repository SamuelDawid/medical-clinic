package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.exceptions.DoctorAlreadyExistsException;
import com.samuelDawid.medical_clinic.exceptions.DoctorNotFoundException;
import com.samuelDawid.medical_clinic.mappers.DoctorMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Doctor;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.DoctorRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository repository;
    private final DoctorMapper mapper;
    private final UserMapper userMapper;
    private final UserPatcher userPatcher;
    private final AffiliationService affiliationService;
    private final EmailValidator emailValidator;


    public List<DoctorDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public DoctorDto findById(@NonNull Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(DoctorNotFoundException::new);
    }

    public DoctorDto create(@NonNull CreateDoctorCommand command) {
        String email = EmailValidator.normalize(command.user().email());
        emailValidator.validate(email);
        validateEmailIsFree(email);

        Doctor doctor = buildDoctor(command, email);
        doctor.setInstitutions(affiliationService.resolveInstitutions(command.institutionId()));

        repository.save(doctor);
        return mapper.toDto(doctor);
    }

    @Transactional
    public DoctorDto update(@NonNull Long id, @NonNull PatchDoctorCommand command) {
        Doctor doctor = repository.findById(id)
                .orElseThrow(DoctorNotFoundException::new);
        doctor.update(command, userPatcher);
        return mapper.toDto(doctor);
    }

    public void delete(@NonNull Long id) {
        Doctor doctor = repository.findById(id).orElseThrow(DoctorNotFoundException::new);
        repository.delete(doctor);
    }

    private Doctor buildDoctor(@NonNull CreateDoctorCommand command, String normalizedEmail) {
        Doctor doctor = mapper.toEntity(command);
        User user = userMapper.toEntity(command.user());
        user.setEmail(normalizedEmail);
        doctor.setUser(user);

        return doctor;
    }

    private void validateEmailIsFree(String email) {
        if (repository.findByUserEmail(email).isPresent()) {
            throw new DoctorAlreadyExistsException();
        }
    }


}
