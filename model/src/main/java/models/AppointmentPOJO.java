package models;

import internalCommandsAndDtos.appointment.CreateAppointmentInternalCommand;
import internalCommandsAndDtos.appointment.PatchAppointmentInternalCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@ToString
@Builder
public class AppointmentPOJO {
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @ToString.Exclude
    private PatientPOJO patient;
    @ToString.Exclude
    private DoctorPOJO doctor;

    public static AppointmentPOJO create(CreateAppointmentInternalCommand command, PatientPOJO patient, DoctorPOJO doctor) {
        return AppointmentPOJO.builder()
                .id(null)
                .startDateTime(command.startDateTime())
                .endDateTime(command.endDateTime())
                .patient(patient)
                .doctor(doctor)
                .build();
    }
    public void updateTimeofTheAppointment(PatchAppointmentInternalCommand command){
        if(Objects.nonNull(command.startTime())){
            this.startDateTime = command.startTime();
        }
        if(Objects.nonNull(command.endTime())){
            this.endDateTime = command.endTime();
        }
    }
    @Override
    public boolean equals(Object o){
        if(this == o) {return true;}
        if(!(o instanceof AppointmentPOJO other)){
            return false;
        }
        return id != null && id.equals(other.getId());
    }
    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
