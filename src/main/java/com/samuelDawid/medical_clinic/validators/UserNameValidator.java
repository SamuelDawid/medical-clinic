package com.samuelDawid.medical_clinic.validators;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserNameValidator implements Validator {
    Pattern userNamePattern = Pattern.compile("[A-Za-z0-9_]+");

    @Override
    public boolean validate(String userName) {
        if (userName.isBlank()) {
            return false;
        }
        Matcher matcher = userNamePattern.matcher(userName);
        return matcher.matches();
    }
}
