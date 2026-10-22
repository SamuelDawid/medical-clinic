package affiliationService;

import doctor.DoctorRepositoryPort;
import exceptions.DoctorNotFoundException;
import exceptions.InstitutionNotFoundException;
import institution.InstitutionRepositoryPort;
import internalCommandsAndDtos.institution.InstitutionDoctorsInternalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.DoctorPOJO;
import models.institution.InstitutionPOJO;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AffiliationService {
    private final DoctorRepositoryPort doctorRepository;
    private final InstitutionRepositoryPort institutionRepository;

    // Woła DoctorService.create przed save – sama nie zapisuje
    public void assignInstitutionsByIdToDoctor(Set<Long> institutionIds, DoctorPOJO doctor) {
        Set<Long> requested = withoutNulls(institutionIds);
        Set<Long> existing = institutionRepository.findExistingIds(requested);
        requested.stream()
                .filter(id -> !existing.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    log.warn("Institution with id {} not found while assigning to doctor", id);
                    throw new InstitutionNotFoundException(id);
                });
        doctor.addInstitutions(requested);
    }

    public InstitutionDoctorsInternalDto assignDoctorsByIdToInstitution(Set<Long> doctorIds, InstitutionPOJO institution) {
        Set<Long> requested = withoutNulls(doctorIds);
        Set<DoctorPOJO> doctors = doctorRepository.findAllById(requested);
        Set<Long> foundIds = doctors.stream().map(DoctorPOJO::getId).collect(Collectors.toSet());
        requested.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    log.warn("Doctor with id {} not found while adding doctors to institution", id);
                    throw new DoctorNotFoundException(id);
                });
        doctors.forEach(doctor -> doctor.addInstitution(institution.getId()));
        doctorRepository.saveAll(doctors);
        log.info("Successfully added doctors to institution {}", institution.getId());
        return new InstitutionDoctorsInternalDto(institution, doctors);
    }

    public Set<DoctorPOJO> showDoctorsByInstitution(Long institutionId) {
        return doctorRepository.findByInstitutionId(institutionId);
    }

    private Set<Long> withoutNulls(Set<Long> ids) {
        if (ids == null) {
            return Set.of();
        }
        return ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}