package com.samuelDawid.medical_clinic.model.institution;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String city;
    private String postCode;
    private String street;
    private String buildingNumber;
}
