package com.medicalclinic.adapter;


import affiliationService.AffiliationService;
import appointment.AppointmentRepositoryPort;
import appointment.AppointmentService;
import appointment.AppointmentServiceImp;
import doctor.DoctorRepositoryPort;
import doctor.DoctorService;
import doctor.DoctorServiceImp;
import institution.InstitutionRepositoryPort;
import institution.InstitutionService;
import institution.InstitutionServiceImp;
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
    public DoctorService doctorService(DoctorRepositoryPort repository,
            final UserRepositoryPort userRepository,
            final AffiliationService affiliationService,
            final EmailValidator emailValidator) {
        return new DoctorServiceImp(repository, userRepository, affiliationService, emailValidator);
    }

    @Bean
    public InstitutionService institutionService(InstitutionRepositoryPort repository,
            AffiliationService affiliationService) {
        return new InstitutionServiceImp(repository, affiliationService);
    }

    @Bean
    public AppointmentService appointmentService(AppointmentRepositoryPort repository,
            DoctorRepositoryPort doctorRepository,
            PatientRepositoryPort patientRepository) {
        return new AppointmentServiceImp(repository, doctorRepository, patientRepository);
    }

    @Bean
    public AffiliationService affiliationService(DoctorRepositoryPort doctorRepository,
            InstitutionRepositoryPort institutionRepository) {
        return new AffiliationService(doctorRepository, institutionRepository);
    }

}
