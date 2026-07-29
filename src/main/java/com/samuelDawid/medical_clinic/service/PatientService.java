package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.exceptions.*;
import com.samuelDawid.medical_clinic.mappers.PatientMapper;
import com.samuelDawid.medical_clinic.mappers.UserMapper;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.model.User;
import com.samuelDawid.medical_clinic.repository.PatientRepository;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository repository;
    private final EmailValidator emailValidator;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    public List<PatientDto> findAll() {
        return repository.findAll().stream().map(patientMapper::toPatientDto).toList();
    }

    public PatientDto findByEmail(@NonNull String email) {
        String normalize = EmailValidator.normalize(email);
        if (!emailValidator.validate(email)) {
            throw new InvalidEmailException(email);
        }
        Patient patient = repository.findByUserEmail(normalize)
                .orElseThrow(() -> new PatientNotFoundException(email));
        return patientMapper.toPatientDto(patient);
    }

    public PatientDto findById(@NotNull Long id) {
        Patient patientToFind = repository.findById(id).orElseThrow(PatientWithIdNotFoundException::new);
        return patientMapper.toPatientDto(patientToFind);
    }

    public PatientDto addPatient(@NonNull CreatePatientCommand patientCommand) {
        String emailNormalizer = EmailValidator.normalize(patientCommand.user().email());

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(patientCommand.user().email());
        }
        if(repository.findByUserEmail(emailNormalizer).isPresent()){
            throw new PatientAlreadyExistsException(emailNormalizer);
        }
        Patient patient = patientMapper.toEntity(patientCommand);
        User user = userMapper.toEntity(patientCommand.user());
        user.setEmail(emailNormalizer);

        patient.setUser(user);
        repository.save(patient);
        return patientMapper.toPatientDto(patient);
    }

    public void deleteByEmail(@NonNull String email) {
        String emailNormalizer = EmailValidator.normalize(email);

        if (!emailValidator.validate(emailNormalizer)) {
            throw new InvalidEmailException(email);
        }
        Patient patientToDelete = repository.findByUserEmail(emailNormalizer)
                .orElseThrow(() -> new PatientNotFoundException(email));
        repository.delete(patientToDelete);
    }

    public void deleteById(@NonNull Long id) {
        Patient patientToDelete = repository.findById(id)
                .orElseThrow(PatientWithIdNotFoundException::new);
        repository.delete(patientToDelete);
    }
    @Transactional
    public PatientDto updatePatient(@NonNull Long id, @NonNull PatchPatientCommand patientCommand) {
        Patient patient = repository.findById(id)
                        .orElseThrow(PatientWithIdNotFoundException::new);

        if(patientCommand.idCardNo() != null){
            patient.setIdCardNo(patientCommand.idCardNo());
        }
        if(patientCommand.birthDay() != null){
            patient.setBirthDay(patientCommand.birthDay());
        }
        if(patientCommand.phoneNumber() != null){
            patient.setPhoneNumber(patientCommand.phoneNumber());
        }
        DoctorService.PatchUser(patientCommand.user(), patient.getUser(), command, doctor);

        return patientMapper.toPatientDto(patient);
    }

    public void updatePassword(@NonNull String newPassword, @NonNull String email) {
        if (newPassword.isBlank()) {
            throw new InvalidPasswordException();
        }
        Patient patientToUpdate = repository.findByUserEmail(EmailValidator.normalize(email))
                .orElseThrow(() -> new PatientNotFoundException(email));
        patientToUpdate.getUser().setPassword(newPassword);
        repository.save(patientToUpdate);
    }
}
