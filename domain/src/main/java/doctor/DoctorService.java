package doctor;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.doctor.CreateDoctorInternalCommand;
import internalCommandsAndDtos.doctor.PatchDoctorInternalCommand;
import models.DoctorPOJO;

public interface DoctorService {
    PageResult<DoctorPOJO> findAll(PageQuery pageQuery);

    DoctorPOJO findById(Long id);

    PageResult<DoctorPOJO> findAll(String speciality, PageQuery pageQuery);

    DoctorPOJO create(CreateDoctorInternalCommand command);

    DoctorPOJO update(Long id, PatchDoctorInternalCommand command);

    void delete(Long id);
}
