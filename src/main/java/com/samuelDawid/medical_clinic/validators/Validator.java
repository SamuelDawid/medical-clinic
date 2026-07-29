package com.samuelDawid.medical_clinic.validators;

import org.springframework.stereotype.Component;

@Component
public interface Validator {
    boolean validate(String email);
}
