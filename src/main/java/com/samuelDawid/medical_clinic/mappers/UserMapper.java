package com.samuelDawid.medical_clinic.mappers;

import com.samuelDawid.medical_clinic.dto.user.CreateUserCommand;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.model.User;
import lombok.NonNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(@NonNull CreateUserCommand command);
    UserDto toDto(@NonNull User user);
}
