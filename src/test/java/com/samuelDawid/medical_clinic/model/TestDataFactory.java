package com.samuelDawid.medical_clinic.model;

import com.samuelDawid.medical_clinic.model.institution.Address;
import com.samuelDawid.medical_clinic.model.institution.Institution;
import com.samuelDawid.medical_clinic.validators.EmailValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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

    public static Doctor buildDoctor(Long id, String medicalSpecialty, Set<Institution> institutions, User user) {
        Doctor doctor = new Doctor(medicalSpecialty, institutions, user);
        ReflectionTestUtils.setField(doctor, "id", id);
        return doctor;
    }

    public static Institution buildInstitution(Long id, String name, Address address, Set<Doctor> doctors) {
        Institution institution = new Institution(name, address, doctors);
        ReflectionTestUtils.setField(institution, "id", id);
        return institution;
    }

    public static List<Institution> threeInstiutions() {
        List<Address> addresses = threeAddresses();
        Institution i1 = buildInstitution(1L, "Centrum Medyczne Alfa", addresses.getFirst(), Set.of());
        Institution i2 = buildInstitution(2L, "Szpital Beta", addresses.get(1), Set.of());
        Institution i3 = buildInstitution(3L, "Klinika Gamma", addresses.getLast(), Set.of());
        return List.of(i1, i2, i3);
    }

    public static List<Address> threeAddresses() {
        Address a1 = new Address(1L, "Warszawa", "00-001", "Marszalkowska", "12A");
        Address a2 = new Address(2L, "Krakow", "30-002", "Florianska", "45");
        Address a3 = new Address(3L, "Gdansk", "80-003", "Dluga", "7B");
        return List.of(a1, a2, a3);
    }

    public static Set<Doctor> threeDoctors() {
        User testUser1 = buildUser(1L, "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1");
        User testUser2 = buildUser(2L, "Piotr",
                "Nowak",
                "piotr.nowak@test.pl",
                "testUser2");
        User testUser3 = buildUser(3L, "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        Doctor doctorTestOne = buildDoctor(1L, "SpecialityOne", Set.of(), testUser1);
        Doctor doctorTestTwo = buildDoctor(2L, "SpecialityTwo", Set.of(), testUser2);
        Doctor doctorTestThree = buildDoctor(3L, "SpecialityThree", Set.of(), testUser3);
        return Set.of(doctorTestOne, doctorTestTwo, doctorTestThree);
    }

    public static List<Patient> threePatients() {
        User testUser1 = buildUser(1L, "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1");
        User testUser2 = buildUser(2L, "Piotr",
                "Nowak",
                "piotr.nowak@test.pl",
                "testUser2");
        User testUser3 = buildUser(3L, "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        Patient test1 = buildPatient(1L, "ABC111111", LocalDate.of(1990, 3, 15), "500100200", testUser1);
        Patient test2 = buildPatient(2L, "ABC222222", LocalDate.of(1985, 7, 22), "500300400", testUser2);
        Patient test3 = buildPatient(3L, "ABC333333", LocalDate.of(2001, 11, 8), "500500600", testUser3);
        return List.of(test1, test2, test3);
    }

    public static List<User> threeUsers() {
        User testUser1 = buildUser(1L, "Anna",
                "Kowalska",
                "anna.kowalska@test.pl",
                "testUser1");
        User testUser2 = buildUser(2L, "Piotr",
                "Nowak",
                "piotr.nowak@test.pl",
                "testUser2");
        User testUser3 = buildUser(3L, "Maria",
                "Wisniewska",
                "maria.wisniewska@test.pl",
                "testUser3");
        return List.of(testUser1, testUser2, testUser3);
    }
}
