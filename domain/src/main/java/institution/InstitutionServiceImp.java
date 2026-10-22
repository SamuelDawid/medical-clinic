package institution;

import affiliationService.AffiliationService;
import exceptions.InstitutionAlreadyExistsException;
import exceptions.InstitutionNotFoundException;
import internalCommands.PageQuery;
import internalCommands.PageResult;
import internalCommands.institution.CreateInstitutionInternalCommand;
import internalCommands.institution.InstitutionDoctorsInternalDto;
import internalCommands.institution.PatchInstitutionInternalCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.DoctorPOJO;
import models.institution.InstitutionPOJO;


import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class InstitutionServiceImp implements InstitutionService {
    private final InstitutionRepositoryPort repository;
    private final AffiliationService affiliationService;

    public PageResult<InstitutionPOJO> findAll(PageQuery pageQuery) {
        return repository.findAll(pageQuery);
    }

    public InstitutionPOJO findById(@NonNull Long id) {
        return findOrThrow(id);
    }

    public InstitutionPOJO create(@NonNull CreateInstitutionInternalCommand command) {
        log.info("Creating Institution");
        InstitutionPOJO institution = InstitutionPOJO.create(command);
        if (repository.existsByName(command.name())) {
            log.warn("Institution with this ane already exists {}", command.name());
            throw new InstitutionAlreadyExistsException();
        }
        InstitutionPOJO saved = repository.save(institution);
        log.info("Created Institution with Id {}", saved.getId());
        return saved;
    }

    public InstitutionDoctorsInternalDto addDoctorsToInstitution(Set<Long> doctorsIdList, Long id) {
        log.info("Adding doctors with Ids {} to Institution {}", doctorsIdList, id);
        InstitutionPOJO institution = findOrThrow(id);
        return affiliationService.assignDoctorsByIdToInstitution(doctorsIdList, institution);
    }

    public Set<DoctorPOJO> showDoctors(Long institutionId) {
        return affiliationService.showDoctorsByInstitution(institutionId);
    }

    public InstitutionPOJO update(@NonNull Long id, @NonNull PatchInstitutionInternalCommand command) {
        log.info("Updating institution {}", id);
        InstitutionPOJO institution = findOrThrow(id);
        institution.update(command);
        log.info("Institution {} updated successfully", id);
        return institution;
    }

    public void delete(@NonNull Long id) {
        log.info("Removing institution {}", id);
        findOrThrow(id);
        repository.delete(id);
        log.info("institution {} removed successfully", id);
    }

    private InstitutionPOJO findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Institution with id {} not found", id);
            return new InstitutionNotFoundException(id);
        });
    }
}
