package com.medicalclinic.adapter.user;

import models.UserPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UserEntityMapper {
    UserPOJO toDomain(UserEntity user);

    UserEntity toEntity(UserPOJO userPOJO);
}
