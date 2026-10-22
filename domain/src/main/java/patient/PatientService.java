package patient;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.patient.CreatePatientInternalCommand;
import internalCommandsAndDtos.patient.PatchPatientInternalCommand;
import models.PatientPOJO;

public interface PatientService {
    void updatePassword(String newPassword, Long id);

    PatientPOJO updatePatient(Long id, PatchPatientInternalCommand patientCommand);

    void deleteById(Long id);

    PatientPOJO create(CreatePatientInternalCommand patientCommand);

    PatientPOJO findById(Long id);

    PageResult<PatientPOJO> findAll(PageQuery pageQuery);

}
