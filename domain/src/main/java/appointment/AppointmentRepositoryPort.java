package appointment;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.critera.AppointmentSearchCriteria;
import internalCommandsAndDtos.critera.AvailableAppointmentCriteria;
import models.AppointmentPOJO;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepositoryPort {
    AppointmentPOJO save(AppointmentPOJO pojo);

    void deleteById(Long id);

    Optional<AppointmentPOJO> findById(Long id);

    Optional<AppointmentPOJO> findWithLockById(Long id);

    PageResult<AppointmentPOJO> findAll(PageQuery pageQuery);

    PageResult<AppointmentPOJO> findAllByPatientId(Long patientId, PageQuery pageQuery);

    PageResult<AppointmentPOJO> search(AppointmentSearchCriteria criteria, PageQuery pageQuery);

    PageResult<AppointmentPOJO> searchAvailable(AvailableAppointmentCriteria criteria, PageQuery pageQuery);

    boolean existsOverlapping(Long doctorId, LocalDateTime start, LocalDateTime end);
}
