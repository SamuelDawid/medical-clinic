package models;

import internalCommands.patient.CreatePatientInternalCommand;
import internalCommands.patient.PatchPatientInternalCommand;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

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

    public static PatientPOJO create(@NonNull CreatePatientInternalCommand command){
        UserPOJO user = UserPOJO.create(command.user());
        return PatientPOJO.builder()
                .id(null)
                .idCardNo(command.idCardNo())
                .birthDay(command.birthDay())
                .phoneNumber(command.phoneNumber())
                .user(user).build();
    }
    public void update(@NonNull PatchPatientInternalCommand patientCommand) {
        if (Objects.nonNull(patientCommand.idCardNo())) {
            this.setIdCardNo(patientCommand.idCardNo());
        }
        if (Objects.nonNull(patientCommand.birthDay())) {
            this.setBirthDay(patientCommand.birthDay());
        }
        if (Objects.nonNull(patientCommand.phoneNumber())) {
            this.setPhoneNumber(patientCommand.phoneNumber());
        }
        if (Objects.nonNull(patientCommand.user())) {
            this.user.update(patientCommand.user());
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
