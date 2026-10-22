package com.medicalclinic.adapter.patient;

import com.medicalclinic.adapter.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PATIENT")
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String idCardNo;
    private LocalDate birthDay;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    @ToString.Exclude
    private UserEntity user;

    public PatientEntity(Long id, String idCardNo, LocalDate birthDay, String phoneNumber, UserEntity user) {
        this.id = id;
        this.idCardNo = idCardNo;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientEntity other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
