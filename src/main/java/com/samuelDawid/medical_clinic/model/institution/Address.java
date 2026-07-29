package com.samuelDawid.medical_clinic.model.institution;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
    String city;
    String postCode;
    String street;
    String buildingNumber;

}
