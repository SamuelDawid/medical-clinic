package com.medicalclinic.adapter.institution;

import models.institution.InstitutionPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstitutionEntityMapper {
    @Mapping(target = "doctors", ignore = true)
    InstitutionEntity toEntity(InstitutionPOJO pojo);
    @Mapping(target = "doctors", ignore = true)
    InstitutionPOJO toDomain(InstitutionEntity entity);
}
