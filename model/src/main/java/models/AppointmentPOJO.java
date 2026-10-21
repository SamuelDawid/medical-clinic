package models;

import lombok.*;

import java.time.LocalDateTime;


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
