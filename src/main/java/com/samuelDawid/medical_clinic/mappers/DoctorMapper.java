package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorSummaryDto;
import com.samuelDawid.medical_clinic.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "institutions", ignore = true)
    Doctor toEntity(CreateDoctorCommand command);

    @Mapping(target = "institutionDto", source = "institutions")
    @Mapping(target = "userDto", source = "user")
    DoctorDto toDto(Doctor doctor);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    DoctorSummaryDto toSummaryDto(Doctor doctor);
}
