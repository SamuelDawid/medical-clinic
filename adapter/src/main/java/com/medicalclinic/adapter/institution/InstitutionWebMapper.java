package com.medicalclinic.adapter.institution;

import com.medicalclinic.adapter.doctor.DoctorWebMapper;
import doctor.DoctorDto;
import institution.CreateInstitutionCommand;
import institution.InstitutionDoctorsDto;
import institution.InstitutionDto;
import institution.PatchInstitutionCommand;
import internalCommandsAndDtos.institution.CreateInstitutionInternalCommand;
import internalCommandsAndDtos.institution.InstitutionDoctorsInternalDto;
import internalCommandsAndDtos.institution.PatchInstitutionInternalCommand;
import models.DoctorPOJO;
import models.institution.InstitutionPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = DoctorWebMapper.class)
public interface InstitutionWebMapper {
    InstitutionDto toDto(InstitutionPOJO institution);

    @Mapping(target = "institutionName", source = "institution.name")
    @Mapping(target = "doctorsDto", source = "doctorsDto")
    InstitutionDoctorsDto toInstitutionDoctorsDto(InstitutionDoctorsInternalDto result);

    Set<DoctorDto> toDoctorDtos(Set<DoctorPOJO> doctors);

    CreateInstitutionInternalCommand toCommand(CreateInstitutionCommand command);

    PatchInstitutionInternalCommand toCommand(PatchInstitutionCommand command);

}
