package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    Doctor toEntity(CreateDoctorCommand command);
    @Mapping(target = "institutionDto",source = "institution")
    @Mapping(target = "userDto", source = "user")
    DoctorDto toDto(Doctor doctor);
}
