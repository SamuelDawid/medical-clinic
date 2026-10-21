package models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
public class PatientPOJO {
    private Long id;
    private String idCardNo;
    private LocalDate birthDay;
    private String phoneNumber;

    @ToString.Exclude
    private UserPOJO user;



    public void update(@NonNull PatchPatientCommand patientCommand, UserPatcher userPatcher) {
        if (patientCommand.idCardNo() != null) {
            this.setIdCardNo(patientCommand.idCardNo());
        }
        if (patientCommand.birthDay() != null) {
            this.setBirthDay(patientCommand.birthDay());
        }
        if (patientCommand.phoneNumber() != null) {
            this.setPhoneNumber(patientCommand.phoneNumber());
        }
        if (patientCommand.user() != null) {
            userPatcher.apply(patientCommand.user(), this.getUser());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
