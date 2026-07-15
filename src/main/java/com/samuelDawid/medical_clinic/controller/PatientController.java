package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.PatientDto;
import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

//    @GetMapping
//    public List<PatientDto> findAll() {
//        // mapper
//        return patientService.all();
//    }

    @GetMapping("/{email}")
    public Patient ById(@PathVariable String email) {
        return patientService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody CreatePatientCommand createDto) {
        return patientService.addNewPatient(new Patient(createDto.getEmail(),
                createDto.getPassword(),
                createDto.getIdCardNo(),
                createDto.getFirstName(),
                createDto.getLastName(),
                createDto.getBirthDay(),
                createDto.getPhoneNumber()
                ));
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Patient update(@PathVariable String email, @RequestBody CreatePatientCommand createDto) {
        Patient newPatient = new Patient(createDto.getEmail(),
                createDto.getPassword(),
                createDto.getIdCardNo(),
                createDto.getFirstName(),
                createDto.getLastName(),
                createDto.getBirthDay(),
                createDto.getPhoneNumber());
        return patientService.update(email, newPatient);
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
