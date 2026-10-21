package com.medicalclinic.adapter.user;

import models.UserPOJO;
import org.mapstruct.Mapper;
import user.UserDto;

@Mapper(componentModel = "spring")
public interface UserWebMapper {
    UserDto toDto(UserPOJO pojo);
}
