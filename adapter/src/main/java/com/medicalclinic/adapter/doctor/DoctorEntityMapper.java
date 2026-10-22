package com.medicalclinic.adapter.doctor;

import com.medicalclinic.adapter.institution.InstitutionEntity;
import com.medicalclinic.adapter.user.UserEntityMapper;
import models.DoctorPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = UserEntityMapper.class)
public interface DoctorEntityMapper {
    @Mapping(target = "institutionsId", source = "institutions")
    DoctorPOJO toDomain(DoctorEntity entity);

    @Mapping(target = "institutions", ignore = true)
    DoctorEntity toEntity(DoctorPOJO doctor);

    default Long institutionToId(InstitutionEntity institution) {
        return institution.getId();
    }
}
