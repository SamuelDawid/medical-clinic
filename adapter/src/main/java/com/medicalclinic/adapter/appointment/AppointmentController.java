package com.medicalclinic.adapter.appointment;

import appointment.AppointmentDto;
import appointment.AppointmentService;
import appointment.AssignPatientToAppointmentCommand;
import appointment.AvailableAppointmentSummary;
import appointment.CreateAppointmentCommand;
import com.medicalclinic.adapter.AdapterControllerCommon;
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

@Tag(name = "Appointments", description = "Operations for managing appointments")
@RequiredArgsConstructor
@RequestMapping("/appointments")
@RestController
public class AppointmentController {
    private final AppointmentService service;
    private final AppointmentWebMapper mapper;

    @Operation(summary = "Search appointments by criteria")
    @ApiResponse(responseCode = "200", description = "Matching appointments")
    @GetMapping
    public PageDto<AppointmentDto> search(@ParameterObject AppointmentSearchParams params,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PageDto.from(service.search(mapper.toCriteria(params), AdapterControllerCommon.toPageQuerry(pageable))
                .map(mapper::toDto));
    }

    @Operation(summary = "Search free appointments")
    @ApiResponse(responseCode = "200", description = "Free appointments")
    @GetMapping("/available")
    public PageDto<AvailableAppointmentSummary> searchAvailable(@ParameterObject AvailableAppointmentParams params,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PageDto.from(service.searchAvailable(mapper.toCriteria(params), AdapterControllerCommon.toPageQuerry(pageable))
                .map(mapper::toAvailableSummary));
    }

    @Operation(summary = "Get appointment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/{id}")
    public AppointmentDto findById(@PathVariable Long id) {
        return mapper.toDto(service.findById(id));
    }

    @Operation(summary = "Get all appointments of a patient")
    @ApiResponse(responseCode = "200", description = "Patient appointments")
    @GetMapping("/patient/{id}")
    public PageDto<AppointmentDto> findAllByPatientId(@PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PageDto.from(service.findAllByPatientId(id, AdapterControllerCommon.toPageQuerry(pageable))
                .map(mapper::toDto));
    }

    @Operation(summary = "Create appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created"),
            @ApiResponse(responseCode = "400", description = "Invalid date or time"),
            @ApiResponse(responseCode = "404", description = "Doctor or patient not found"),
            @ApiResponse(responseCode = "409", description = "Time overlaps with another appointment")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto create(@RequestBody CreateAppointmentCommand command) {
        return mapper.toDto(service.create(mapper.toCommand(command)));
    }

    @Operation(summary = "Assign patient to appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient assigned"),
            @ApiResponse(responseCode = "404", description = "Appointment or patient not found"),
            @ApiResponse(responseCode = "409", description = "Appointment already taken")
    })
    @PutMapping
    public AppointmentDto assignPatientToAppointment(@RequestBody AssignPatientToAppointmentCommand command) {
        return mapper.toDto(service.assignPatientToAppointment(mapper.toCommand(command)));
    }

    @Operation(summary = "Cancel patient's appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient removed from appointment"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PatchMapping("/cancel/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patientAppointmentCancel(@PathVariable Long appointmentId) {
        service.removePatientFromVisit(appointmentId);
    }

    @Operation(summary = "Delete appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment deleted"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}