package com.medicalclinic.adapter.patient;


import com.medicalclinic.adapter.AdapterControllerCommon;
import common.ChangePasswordCommand;
import internalCommandsAndDtos.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import models.PatientPOJO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import page.PageDto;
import patient.CreatePatientCommand;
import patient.PatchPatientCommand;
import patient.PatientDto;
import patient.PatientService;


@Tag(name = "Patients", description = "Operations for managing patient records")
@RequestMapping("/patients")
@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientWebMapper mapper;
    @Operation(summary = "Get all available Patients")
    @ApiResponse(responseCode = "200", description = "Returned all patients")
    @GetMapping
    public PageDto<PatientDto> findAll(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        PageResult<PatientPOJO> result = patientService.findAll(AdapterControllerCommon.toPageQuerry(pageable));
        return PageDto.from(result.map(mapper::toDto));
    }

    @Operation(summary = "Get patient by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied")
    })
    @GetMapping("/{id}")
    public PatientDto byId(@PathVariable Long id) {
        return mapper.toDto(patientService.findById(id));
    }

    @Operation(summary = "Create Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Patient details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@RequestBody CreatePatientCommand createDto) {
        return mapper.toDto(patientService.create(mapper.toCreateCommand(createDto)));
    }

    @Operation(summary = "Updates Patient Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto update(@PathVariable Long id, @RequestBody PatchPatientCommand patientCommand) {
        return mapper.toDto(patientService.updatePatient(id, mapper.toPatchCommand(patientCommand)));
    }

    @Operation(summary = "Change Patient Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password supplied"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable long id, @RequestBody ChangePasswordCommand newPassword) {
        patientService.updatePassword(newPassword.newPassword(), id);
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
