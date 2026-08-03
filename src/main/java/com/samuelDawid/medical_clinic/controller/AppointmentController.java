package com.samuelDawid.medical_clinic.controller;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.appointment.CreateAppointmentCommand;
import com.samuelDawid.medical_clinic.service.AppointmentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController("/appointments")
public class AppointmentController {
    private final AppointmentService service;

    @GetMapping
    public Set<AppointmentDto> findAll(){
        return service.findAll();
    }

    @GetMapping("{id}")
    public AppointmentDto findById(@PathVariable @NonNull Long id){
        return service.findById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto create(@RequestBody CreateAppointmentCommand command){
        return service.create(command);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NonNull Long id){
        service.delete(id);
    }
}
