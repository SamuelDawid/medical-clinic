package institution;

import internalCommands.PageQuery;
import internalCommands.PageResult;
import models.institution.InstitutionPOJO;

import java.util.Optional;
import java.util.Set;

public interface InstitutionRepositoryPort {
    boolean existsByName(String name);

    PageResult<InstitutionPOJO> findAll(PageQuery pageQuery);

    InstitutionPOJO save(InstitutionPOJO institutionPOJO);

    Optional<InstitutionPOJO> findById(Long id);

    Set<Long> findExistingIds(Set<Long> ids);

    void delete(Long id);
}
