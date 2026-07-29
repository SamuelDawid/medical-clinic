package com.samuelDawid.medical_clinic.service;

import com.samuelDawid.medical_clinic.dto.user.PatchUserCommand;
import com.samuelDawid.medical_clinic.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserPatcher {
    public void apply(PatchUserCommand command, User user) {
        if (command == null) return;
        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.lastName()  != null) user.setLastName(command.lastName());
        if (command.email()     != null) user.setEmail(command.email());
    }
}
