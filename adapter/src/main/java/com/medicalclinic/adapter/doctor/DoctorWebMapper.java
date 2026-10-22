package com.medicalclinic.adapter.doctor;

import doctor.CreateDoctorCommand;
import doctor.DoctorDto;
import doctor.DoctorSummaryDto;
import doctor.PatchDoctorCommand;
import internalCommandsAndDtos.doctor.CreateDoctorInternalCommand;
import internalCommandsAndDtos.doctor.DoctorSummaryInternalDto;
import internalCommandsAndDtos.doctor.PatchDoctorInternalCommand;
import models.DoctorPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorWebMapper {
    DoctorDto toDto(DoctorPOJO pojo);
    CreateDoctorCommand toCreateCommand(CreateDoctorInternalCommand command);
    PatchDoctorCommand toPatchCommand(PatchDoctorInternalCommand command);
    DoctorSummaryDto toSummaryDto(DoctorSummaryInternalDto summaryInternalDto);
}
