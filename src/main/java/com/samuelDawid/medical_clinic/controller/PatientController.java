package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.ChangePasswordCommand;
import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.patient.CreatePatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatchPatientCommand;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Patients", description = "Operations for managing patient records")
@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all available Patients")
    @ApiResponse(responseCode = "200", description = "Returned all patients")
    @GetMapping
    public PageDto<PatientDto> findAll(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return patientService.findAll(pageable);
    }

    @Operation(summary = "Get patient by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied")
    })
    @GetMapping("/{id}")
    public PatientDto byId(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @Operation(summary = "Create Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Patient details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody CreatePatientCommand createDto) {
        return patientService.addPatient(createDto);
    }

    @Operation(summary = "Updates Patient Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto update(@PathVariable Long id, @RequestBody PatchPatientCommand createDto) {
        return patientService.updatePatient(id, createDto);
    }

    @Operation(summary = "Change Patient Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable String email, @RequestBody ChangePasswordCommand newPassword) {
        patientService.updatePassword(newPassword.newPassword(), email);
    }

    @Operation(summary = "Delete Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/email/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        patientService.deleteByEmail(email);
    }

    @Operation(summary = "Delete Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.deleteById(id);
    }
}
