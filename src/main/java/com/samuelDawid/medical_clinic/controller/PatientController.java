package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.model.Patient;
import com.samuelDawid.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    //CRUD

    @GetMapping
    public List<Patient> findAll(){
        return patientService.all();
    }

    @GetMapping("/{email}")
    public Patient getById(@PathVariable String email){
        return patientService.findByEmail(email);
    }

    @PostMapping
    public Patient create(@RequestBody Patient patient){
        return patientService.addNewPatient(patient);
    }

    @PutMapping("/{email}")
    public Patient update(@PathVariable String email,@RequestBody Patient patient){
        return patientService.update(email,patient);
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email){
         patientService.deleteByEmail(email);
    }
}
