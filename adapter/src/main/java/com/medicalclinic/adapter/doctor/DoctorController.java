package com.medicalclinic.adapter.doctor;

import com.medicalclinic.adapter.AdapterControllerCommon;
import doctor.*;
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
import page.PageDto;

@Tag(name = "Doctors", description = "Operations for managing doctors records")
@RequestMapping("/doctors")
@RestController
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;
    private final DoctorWebMapper mapper;

    @Operation(summary = "Get all doctors")
    @ApiResponse(description = "Get all doctors", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PageDto<DoctorSummaryDto> findAll(@RequestParam(required = false) String speciality,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PageDto.from(service.findAll(speciality, AdapterControllerCommon.toPageQuerry(pageable))
                .map(mapper::toSummaryDto));
    }

    @Operation(summary = "Get by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable Long id) {
        return mapper.toDto(service.findById(id));
    }

    @Operation(summary = "Create Doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Doctor details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto create(@RequestBody CreateDoctorCommand command) {
        return mapper.toDto(service.create(mapper.toCommand(command)));
    }

    @Operation(summary = "Update Doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDto update(@PathVariable Long id, @RequestBody PatchDoctorCommand command) {
        return mapper.toDto(service.update(id, mapper.toCommand(command)));
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
