package doctor;

import internalCommands.PageQuery;
import internalCommands.PageResult;
import internalCommands.doctor.CreateDoctorInternalCommand;
import internalCommands.doctor.PatchDoctorInternalCommand;
import models.DoctorPOJO;

public interface DoctorService {
    PageResult<DoctorPOJO> findAll(PageQuery pageQuery);

    DoctorPOJO findById(Long id);

    PageResult<DoctorPOJO> findAll(String speciality, PageQuery pageQuery);

    DoctorPOJO create(CreateDoctorInternalCommand command);

    DoctorPOJO update(Long id, PatchDoctorInternalCommand command);

    void delete(Long id);
}
