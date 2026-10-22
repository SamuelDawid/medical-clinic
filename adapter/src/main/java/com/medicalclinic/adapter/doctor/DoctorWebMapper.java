package com.medicalclinic.adapter.doctor;

import doctor.CreateDoctorCommand;
import doctor.DoctorDto;
import doctor.DoctorSummaryDto;
import doctor.PatchDoctorCommand;
import internalCommandsAndDtos.doctor.CreateDoctorInternalCommand;
import internalCommandsAndDtos.doctor.PatchDoctorInternalCommand;
import models.DoctorPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorWebMapper {
    @Mapping(target = "userDto", source = "user")
    @Mapping(target = "institutionIds", source = "institutionsId")
    DoctorDto toDto(DoctorPOJO doctor);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    DoctorSummaryDto toSummaryDto(DoctorPOJO doctor);

    CreateDoctorInternalCommand toCommand(CreateDoctorCommand command);

    PatchDoctorInternalCommand toCommand(PatchDoctorCommand command);
}
