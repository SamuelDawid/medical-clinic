package appointment;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.appointment.AssignPatientToAppointmentInternalCommand;
import internalCommandsAndDtos.appointment.CreateAppointmentInternalCommand;
import internalCommandsAndDtos.critera.AppointmentSearchCriteria;
import internalCommandsAndDtos.critera.AvailableAppointmentCriteria;
import models.AppointmentPOJO;

public interface AppointmentService {
    PageResult<AppointmentPOJO> findAll(PageQuery pageQuery);

    PageResult<AppointmentPOJO> search(AppointmentSearchCriteria criteria, PageQuery pageQuery);

    PageResult<AppointmentPOJO> searchAvailable(AvailableAppointmentCriteria criteria, PageQuery pageQuery);

    PageResult<AppointmentPOJO> findAllByPatientId(Long id, PageQuery pageQuery);

    AppointmentPOJO findById(Long id);

    AppointmentPOJO create(CreateAppointmentInternalCommand command);

    void removePatientFromVisit(Long appointmentId);

    AppointmentPOJO assignPatientToAppointment(AssignPatientToAppointmentInternalCommand command);

    void delete(Long appointmentId);

}
