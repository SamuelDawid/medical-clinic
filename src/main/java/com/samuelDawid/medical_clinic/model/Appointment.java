package com.samuelDawid.medical_clinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @ToString.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @ToString.Exclude
    private Doctor doctor;

    public Appointment(LocalDateTime startDateTime, LocalDateTime endDateTime, Patient patient, Doctor doctor) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.patient = patient;
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {return true;}
        if(!(o instanceof Appointment other)){
            return false;
        }
        return id != null && id.equals(other.getId());
    }
    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
