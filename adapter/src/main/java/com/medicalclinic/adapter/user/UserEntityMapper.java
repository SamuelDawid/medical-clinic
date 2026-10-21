package com.medicalclinic.adapter.user;

import models.UserPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UserEntityMapper {
    UserPOJO toDomain(User user);

    User toEntity(UserPOJO userPOJO);
}
