package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.PatchDoctorCommand;
import com.samuelDawid.medical_clinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctor", description = "Operations for managing doctors records")
@RequestMapping("/doctor")
@RestController
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;

    @Operation(summary = "Get all doctors")
    @ApiResponse(description = "Get all doctors", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<DoctorDto> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Get by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Create Doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Doctor Institution details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto create(@RequestBody CreateDoctorCommand command) {
        return service.create(command);
    }

    @Operation(summary = "Update Doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Doctor id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDto update(@PathVariable Long id, @RequestBody PatchDoctorCommand command) {
        return service.update(id, command);
    }

    @Operation(summary = "Delete Doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
