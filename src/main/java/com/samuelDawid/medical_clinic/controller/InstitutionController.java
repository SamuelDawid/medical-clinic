package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.PageDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDoctorsDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import com.samuelDawid.medical_clinic.service.InstitutionService;
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

import java.util.Set;

@Tag(name = "Institution", description = "Operations for managing institution records")
@RequestMapping("/institutions")
@RestController
@RequiredArgsConstructor
public class InstitutionController {
    private final InstitutionService service;

    @Operation(summary = "Get all Institutions")
    @ApiResponse(description = "Get all Institutions", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PageDto<InstitutionDto> findAll(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }

    @Operation(summary = "Get by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Institution found"),
            @ApiResponse(responseCode = "404", description = "Institution not found"),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public InstitutionDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Get doctors from Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found"),
            @ApiResponse(responseCode = "404", description = "Institution not found"),
    })
    @GetMapping("/{id}/doctors")
    @ResponseStatus(HttpStatus.OK)
    public Set<DoctorDto> findDoctorsByInstitutionName(@PathVariable Long id) {
        return service.showDoctors(id);
    }

    @Operation(summary = "Add list of doctors to Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors added Successfully")
    })
    @PatchMapping("/institution/{id}/doctors")
    @ResponseStatus(HttpStatus.OK)
    public InstitutionDoctorsDto addDoctorsToInstitution(@RequestBody Set<Long> doctorsId, @PathVariable Long id) {
        return service.addDoctorsToInstitution(doctorsId, id);
    }

    @Operation(summary = "Create Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Institution Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Institution details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto create(@RequestBody CreateInstitutionCommand command) {
        return service.create(command);
    }

    @Operation(summary = "Update Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Institution Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InstitutionDto update(@PathVariable Long id, @RequestBody PatchInsitutionCommand command) {
        return service.update(id, command);
    }

    @Operation(summary = "Delete Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Institution Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
