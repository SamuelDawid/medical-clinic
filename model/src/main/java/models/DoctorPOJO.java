package models;

import internalCommandsAndDtos.doctor.CreateDoctorInternalCommand;
import internalCommandsAndDtos.doctor.PatchDoctorInternalCommand;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DoctorPOJO {
    private Long id;
    private String medicalSpecialty;
    @ToString.Exclude
    @Builder.Default
    private Set<Long> institutionsId = new HashSet<>();
    @ToString.Exclude
    private UserPOJO user;

    public static DoctorPOJO create(CreateDoctorInternalCommand command) {
        return DoctorPOJO.builder()
                .id(null)
                .medicalSpecialty(command.medicalSpecialty())
                .institutionsId(command.institutionId())
                .user(UserPOJO.create(command.user()))
                .build();
    }

    public void update(PatchDoctorInternalCommand command) {
        if (Objects.nonNull(command.medicalSpecialty())) {
            this.setMedicalSpecialty(command.medicalSpecialty());
        }
        if (Objects.nonNull(command.user())) {
            this.user.update(command.user());
        }
    }

    public void addInstitutions(Set<Long> ids) {
        this.institutionsId.addAll(ids);
    }

    public void addInstitution(Long id) {
        this.institutionsId.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoctorPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
