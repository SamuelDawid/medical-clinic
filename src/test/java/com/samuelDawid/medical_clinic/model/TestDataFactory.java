package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.dto.appointment.AppointmentDto;
import com.samuelDawid.medical_clinic.dto.doctor.DoctorDto;
import com.samuelDawid.medical_clinic.dto.institution.InstitutionDto;
import com.samuelDawid.medical_clinic.dto.patient.PatientDto;
import com.samuelDawid.medical_clinic.dto.user.UserDto;
import com.samuelDawid.medical_clinic.model.institution.Address;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TestDataFactory {
    public static User buildUser(Long id, String firstName, String lastName, String email, String password) {
        User user = new User(firstName, lastName, email, password);
        String normalizedEmail = EmailValidator.normalize(email);
        user.setEmail(normalizedEmail);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static Patient buildPatient(Long id, String idCardNo, LocalDate birthDay, String phoneNumber, User user) {
        Patient patient = new Patient(idCardNo, birthDay, phoneNumber, user);
        ReflectionTestUtils.setField(patient, "id", id);
        return patient;
    }

    public static Doctor buildDoctor(Long id, String medicalSpecialty, User user) {
        Doctor doctor = new Doctor(medicalSpecialty, user);
        ReflectionTestUtils.setField(doctor, "id", id);
        return doctor;
    }

    public static Institution buildInstitution(Long id, String name, Address address, Set<Doctor> doctors) {
        Institution institution = new Institution(name, address, doctors);
        ReflectionTestUtils.setField(institution, "id", id);
        return institution;
    }

    public static List<AppointmentDto> threeAppointmentsFotTheSamePatient(String patientName) {
        return List.of(
                new AppointmentDto(
                        1L,
                        LocalDateTime.of(2026, 9, 1, 9, 15),
                        LocalDateTime.of(2026, 9, 1, 10, 15),
                        "John Doe",
                        patientName
                ),
                new AppointmentDto(
                        2L,
                        LocalDateTime.of(2026, 9, 3, 13, 30),
                        LocalDateTime.of(2026, 9, 3, 14, 15),
                        "Emily Smith",
                        patientName
                ),
                new AppointmentDto(
                        3L,
                        LocalDateTime.of(2026, 9, 8, 15, 45),
                        LocalDateTime.of(2026, 9, 8, 16, 30),
                        "Michael Johnson",
                        patientName
                )
        );

    }
    public static List<UserDto> threeUsersDto = List.of(
            new UserDto(1L,
                    "Anna",
                    "Kowalska",
                    "anna.kowalska@test.pl"),
            new UserDto(2L,
                    "Piotr",
                    "Nowak",
                    "piotr.nowak@test.pl"),
            new UserDto(3L,
                    "Maria",
                    "Wisniewska",
                    "maria.wisniewska@test.pl")
    );
    public static List<DoctorDto> threeDoctorsDto(){
        List<UserDto> users = threeUsersDto;
        List<InstitutionDto> institutionDto = threeInstitutionsDto();
        return List.of(
                new DoctorDto(
                        1L,
                        "Kardiologia",
                        Set.of(institutionDto.getFirst()),
                        users.getFirst()
                ),
                new DoctorDto(
                        2L,
                        "Ortopedia",
                        Set.of(institutionDto.get(1)),
                        users.get(1)
                ),
                new DoctorDto(
                        3L,
                        "Dermatologia",
                        Set.of(institutionDto.get(2)),
                        users.getLast()
                ));
    }

    public static List<InstitutionDto> threeInstitutionsDto() {
        List<Address> addresses = threeAddresses();
        return List.of(
                new InstitutionDto(
                        1L,
                        "Szpital Kliniczny im. Jana Pawła II",
                        addresses.getFirst()
                ),
                new InstitutionDto(
                        2L,
                        "Centrum Medyczne Nowe Zdrowie",
                        addresses.get(1)
                ),
                new InstitutionDto(
                        3L,
                        "Wojewódzki Szpital Specjalistyczny",
                        addresses.getLast()
                )
        );
    }

    public static List<AppointmentDto> threeAppointmentsDto() {
        AppointmentDto appointmentDtoFirst = new AppointmentDto(
                1L,
                LocalDateTime.of(2026, 9, 15, 15, 30),
                LocalDateTime.of(2026, 9, 15, 16, 15),
                "Anna Kowalska",
                "Piotr Nowak"
        );
        AppointmentDto appointmentDtoSecond = new AppointmentDto(
                2L,
                LocalDateTime.of(2026, 9, 15, 11, 30),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                "Robert Berathion",
                "Jhon Snow"
        );
        AppointmentDto appointmentDtoThird = new AppointmentDto(
                3L,
                LocalDateTime.of(2026, 9, 15, 10, 45),
                LocalDateTime.of(2026, 9, 15, 11, 15),
                "Hubert Piwowarczyk",
                "Alicja Nowak"
        );
        return List.of(appointmentDtoFirst, appointmentDtoSecond, appointmentDtoThird);
    }
    public static List<PatientDto> threePatientDto(){

        return List.of(
                new PatientDto(1L,
                        threeUsersDto.getFirst(),
                        LocalDate.of(2000,1,11),
                        "111-222-333"),
                new PatientDto(2L,
                        threeUsersDto.get(1),
                        LocalDate.of(2005,5,15),
                        "222-333-444"),
                new PatientDto(3L,
                        threeUsersDto.getLast(),
                        LocalDate.of(1995,6,11),
                        "333-444-555")
        );
    }
    public static List<Appointment> threeAppointments() {
        List<Doctor> doctors = threeDoctors();
        List<Patient> patients = threePatients();
        Appointment appointmentOne = new Appointment(
                LocalDateTime.of(2026, 9, 15, 15, 30),
                LocalDateTime.of(2026, 9, 15, 16, 15),
                patients.getFirst(),
                doctors.getFirst()
        );
        Appointment appointmentTwo = new Appointment(
                LocalDateTime.of(2026, 9, 15, 11, 30),
                LocalDateTime.of(2026, 9, 15, 12, 0),
                patients.getFirst(),
                doctors.getFirst()
        );
        Appointment appointmentThree = new Appointment(
                LocalDateTime.of(2026, 9, 15, 10, 45),
                LocalDateTime.of(2026, 9, 15, 11, 15),
                patients.getFirst(),
                doctors.getFirst()
        );
        return List.of(appointmentOne, appointmentTwo, appointmentThree);
    }

    public static List<Institution> threeInstitutions() {
        List<Address> addresses = threeAddresses();
        List<Doctor> doctors = threeDoctors();
        Institution i1 = buildInstitution(1L,
                "Centrum Medyczne Alfa",
                addresses.getFirst(),
                Set.of(doctors.getFirst(), doctors.get(1)));
        Institution i2 = buildInstitution(2L,
                "Szpital Beta",
                addresses.get(1),
                Set.of());
        Institution i3 = buildInstitution(3L,
                "Klinika Gamma",
                addresses.getLast(),
                Set.of());
        return List.of(i1, i2, i3);
    }

    public static List<Address> threeAddresses() {
        Address a1 = new Address(1L,
                "Warszawa",
                "00-001",
                "Marszalkowska",
                "12A");
        Address a2 = new Address(2L, "Krakow", "30-002", "Florianska", "45");
        Address a3 = new Address(3L, "Gdansk", "80-003", "Dluga", "7B");
        return List.of(a1, a2, a3);
    }

    public static List<Doctor> threeDoctors() {
        List<User> users = threeUsers();
        Doctor doctorTestOne = buildDoctor(1L,
                "SpecialityOne",
                users.getFirst());
        Doctor doctorTestTwo = buildDoctor(2L,
                "SpecialityTwo",
                users.get(1));
        Doctor doctorTestThree = buildDoctor(3L,
                "SpecialityThree",
                users.getLast());
        return List.of(doctorTestOne, doctorTestTwo, doctorTestThree);
    }

    public static List<Patient> threePatients() {
        List<User> users = threeUsers();
        Patient test1 = buildPatient(1L,
                "ABC111111",
                LocalDate.of(1990, 3, 15),
                "500100200",
                users.getFirst());
        Patient test2 = buildPatient(2L,
                "ABC222222",
                LocalDate.of(1985, 7, 22),
                "500300400",
                users.get(1));
        Patient test3 = buildPatient(3L,
                "ABC333333",
                LocalDate.of(2001, 11, 8),
                "500500600",
                users.getLast());
        return List.of(test1, test2, test3);
    }

    public static List<User> threeUsers() {
        User testUser1 = buildUser(1L,
                "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1");
        User testUser2 = buildUser(2L,
                "Piotr",
                "Nowak",
                "piotr.nowak@test.pl",
                "testUser2");
        User testUser3 = buildUser(3L,
                "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        return List.of(testUser1, testUser2, testUser3);
    }
}
