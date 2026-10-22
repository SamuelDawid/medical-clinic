package doctor;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import models.DoctorPOJO;

import java.util.Optional;
import java.util.Set;

public interface DoctorRepositoryPort {
    PageResult<DoctorPOJO> findAll(PageQuery pageQuery);
    Set<DoctorPOJO> findByInstitutionsId(Long institutionId);
    DoctorPOJO save(DoctorPOJO doctorPOJO);
    Optional<DoctorPOJO> findByIdForUpdate(Long id);
    Optional<DoctorPOJO> findById(Long id);
    PageResult<DoctorPOJO> findByMedicalSpecialtyIgnoreCase(String speciality,PageQuery pageQuery);
    void delete(Long id);
    Set<DoctorPOJO> findAllById(Set<Long> ids);
    Set<DoctorPOJO> findByInstitutionId(Long institutionId);
    void saveAll(Set<DoctorPOJO> doctors);
}
