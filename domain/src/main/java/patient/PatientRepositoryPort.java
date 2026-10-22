package patient;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import models.PatientPOJO;

import java.util.Optional;

public interface PatientRepositoryPort {
    PageResult<PatientPOJO> findAll(PageQuery pageQuery);
    PatientPOJO save(PatientPOJO patient);
    Optional<PatientPOJO> findById(Long id);
    void delete(Long id);
}
