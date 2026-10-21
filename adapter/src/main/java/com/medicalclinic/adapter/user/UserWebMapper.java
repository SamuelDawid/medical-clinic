package com.medicalclinic.adapter.user;

import common.ChangePasswordCommand;
import internalCommands.ChangePasswordInternalCommand;
import internalCommands.user.CreateUserInternalCommand;
import models.UserPOJO;
import org.mapstruct.Mapper;
import user.CreateUserCommand;
import user.UserDto;

@Mapper(componentModel = "spring")
public interface UserWebMapper {
    UserDto toDto(UserPOJO pojo);
    CreateUserInternalCommand toCreateCommand(CreateUserCommand command);
    ChangePasswordInternalCommand toChangePasswordCommand(ChangePasswordCommand command);
}
