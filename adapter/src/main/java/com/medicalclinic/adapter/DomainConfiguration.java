package com.medicalclinic.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.UserRepositoryPort;
import user.UserService;
import user.UserServiceImp;
import validators.EmailValidator;

@Configuration
public class DomainConfiguration {
    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }

    @Bean
    public UserService userService(UserRepositoryPort port, EmailValidator emailValidator) {
        return new UserServiceImp(port, emailValidator);
    }
}
