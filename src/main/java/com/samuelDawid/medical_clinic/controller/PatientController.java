package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.patientMapper.PatientMapper;
import com.samuelDawid.medical_clinic.service.PatientService;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<PatientDto> findAll() {
        // mapper
        return patientService.all().stream().map(PatientMapper::toPatientDto).toList();
    }

    @GetMapping("/{email}")
    public Patient ById(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody CreatePatientCommand createDto) {
        Patient newPatient = PatientMapper.toEntity(createDto);
        patientService.addNewPatient(newPatient);
        return PatientMapper.toPatientDto(newPatient);

    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public PatientDto update(@PathVariable String email, @RequestBody CreatePatientCommand createDto) {
        EmailValidator.normalize(email);
        Patient newPatient = new Patient(email,createDto.password(),createDto.idCardNo(),createDto.firstName(),createDto.lastName(),createDto.birthDay(),createDto.phoneNumber());
        patientService.update(email, newPatient);
        return PatientMapper.toPatientDto(newPatient);
    }

    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable String email, @RequestBody String newPassword) {
        patientService.updatePassword(newPassword, email);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        patientService.deleteByEmail(email);
    }


}
