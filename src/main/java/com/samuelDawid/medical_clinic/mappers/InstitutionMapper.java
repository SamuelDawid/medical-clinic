package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.AddressDto;
import com.samuelDawid.medical_clinic.dto.CreateAddressCommand;
import com.samuelDawid.medical_clinic.dto.institution.CreateInstitutionCommand;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.model.institution.Address;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "doctors",ignore = true)
    Institution toEntity(CreateInstitutionCommand command);

    InstitutionDto toDto(Institution institution);

    Address toEntity(CreateAddressCommand command);

    AddressDto toDto(Address address);
}
