package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id",ignore = true)
    User toEntity(CreateUserCommand command);

    UserDto toDto(User user);
}
