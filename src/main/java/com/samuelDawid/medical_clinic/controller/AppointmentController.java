package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.AssignPatientToAppointmentCommand;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Appointments", description = "Operations for managing Appointments records")
@RequiredArgsConstructor
@RequestMapping("/appointments")
@RestController()
public class AppointmentController {
    private final AppointmentService service;

    @Operation(summary = "Get all appointments")
    @ApiResponse(description = "Get all appointments", responseCode = "200")
    @GetMapping
    public Set<AppointmentDto> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Get appointment by id")
    @ApiResponse(description = "appointment found", responseCode = "200")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @GetMapping("{id}")
    public AppointmentDto findById(@PathVariable @NonNull Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Get appointments by Patient id")
    @ApiResponse(description = "appointments found", responseCode = "200")
    @GetMapping("/patient/{id}")
    public Set<AppointmentDto> findAllByPatientId(@PathVariable @NonNull Long id) {
        return service.findAllByPatientId(id);
    }

    @Operation(summary = "Create appointment, patient does not need to be assigned")
    @ApiResponse(responseCode = "201", description = "Appointment created")
    @ApiResponse(responseCode = "400", description = "Appointment is in the past or does not start at a full quarter of an hour")
    @ApiResponse(responseCode = "404", description = "Doctor or patient not found")
    @ApiResponse(responseCode = "409", description = "Appointment overlaps with another appointment of this doctor")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto create(@RequestBody CreateAppointmentCommand command) {
        return service.create(command);
    }

    @Operation(summary = "Assign patient to appointment")
    @ApiResponse(responseCode = "200", description = "Patient assigned successfully")
    @ApiResponse(responseCode = "400", description = "Appointment is already in the past")
    @ApiResponse(responseCode = "404", description = "Appointment or patient not found")
    @ApiResponse(responseCode = "409", description = "Appointment is already taken")
    @PutMapping()
    public AppointmentDto assignPatientToAppointment(@RequestBody AssignPatientToAppointmentCommand command) {
        return service.assignPatientToAppointment(command);
    }

    @Operation(summary = "Delete Appointment")
    @ApiResponse(description = "Appointment deleted successfully", responseCode = "204")
    @ApiResponse(description = "Appointment not found", responseCode = "404")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NonNull Long id) {
        service.delete(id);
    }
}
