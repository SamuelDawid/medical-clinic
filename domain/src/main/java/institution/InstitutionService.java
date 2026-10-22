package institution;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.institution.CreateInstitutionInternalCommand;
import internalCommandsAndDtos.institution.InstitutionDoctorsInternalDto;
import internalCommandsAndDtos.institution.PatchInstitutionInternalCommand;
import models.DoctorPOJO;
import models.institution.InstitutionPOJO;

import java.util.Set;

public interface InstitutionService {
    PageResult<InstitutionPOJO> findAll(PageQuery pageQuery);

    InstitutionPOJO findById(Long id);

    InstitutionPOJO create(CreateInstitutionInternalCommand command);

    InstitutionDoctorsInternalDto addDoctorsToInstitution(Set<Long> doctorsIdList, Long id);

    Set<DoctorPOJO> showDoctors(Long institutionId);

    InstitutionPOJO update(Long id, PatchInstitutionInternalCommand command);

    void delete(Long id);
}
