package com.medicalclinic.adapter;


import affiliationService.AffiliationService;
import doctor.DoctorRepositoryPort;
import institution.InstitutionRepositoryPort;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import patient.PatientRepositoryPort;
import patient.PatientService;
import patient.PatientServiceImp;
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

    @Bean
    public PatientService patientService(PatientRepositoryPort repository,
            UserRepositoryPort userRepository,
            EmailValidator emailValidator) {
        return new PatientServiceImp(repository, userRepository, emailValidator);
    }

    @Bean
    public OpenAPI medicalClinicOpenAPI() {
        return new OpenAPI().info(
                new Info().title("Medical Clinic API")
                        .version("1.0")
                        .description("API for managing patients at the medical clinic")
        );
    }
    @Bean
    public AffiliationService affiliationService(DoctorRepositoryPort doctorRepository,
            InstitutionRepositoryPort institutionRepository) {
        return new AffiliationService(doctorRepository, institutionRepository);
    }

}
