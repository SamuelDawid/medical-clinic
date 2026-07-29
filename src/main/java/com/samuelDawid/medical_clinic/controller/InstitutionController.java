package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.institution.PatchInsitutionCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Institution", description = "Operations for managing institution records")
@RequestMapping("/institutions")
@RestController
@RequiredArgsConstructor
public class InstitutionController {
    private final InstitutionController controller;

    @Operation(summary = "Get all users")
    @ApiResponse(description = "Get all users", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<InstitutionDto> findAll() {
        return controller.findAll();
    }

    @Operation(summary = "Get by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Institution found"),
            @ApiResponse(responseCode = "404", description = "Institution not found"),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public InstitutionDto findById(@PathVariable Long id) {
        return controller.findById(id);
    }

    @Operation(summary = "Create Institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Institution Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Institution details")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto create(@RequestBody CreateInstitutionCommand command) {
        return controller.create(command);
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
        return controller.update(id, command);
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
         controller.delete(id);
    }
}
