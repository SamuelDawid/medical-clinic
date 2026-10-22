package affiliationService;


import doctor.DoctorRepositoryPort;
import exceptions.DoctorNotFoundException;
import exceptions.InstitutionNotFoundException;
import institution.InstitutionRepositoryPort;
import internalCommands.institution.InstitutionDoctorsInternalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.DoctorPOJO;
import models.institution.InstitutionPOJO;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AffiliationService {
    private final DoctorRepositoryPort doctorRepository;
    private final InstitutionRepositoryPort institutionRepository;

    public Set<InstitutionPOJO> resolveInstitutions(Set<Long> institutionsIds) {
        return institutionsIds.stream()
                .filter(Objects::nonNull)
                .map(institutionRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<DoctorPOJO> resolveDoctors(Set<Long> doctorsIds) {
        return doctorsIds.stream()
                .filter(Objects::nonNull)
                .map(doctorRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
    public void assignInstitutionsByIdToDoctor(Set<Long> institutionsId, DoctorPOJO doctor){
        Set<InstitutionPOJO> institutions = resolveInstitutions(institutionsId);
        checkIfAllInstitutionsWereAdded(institutionsId,institutions);
        if(!doctor.getInstitutions().isEmpty()){
            institutions.addAll(doctor.getInstitutions());
        }
        doctor.setInstitutions(institutions);
    }
    public InstitutionDoctorsInternalDto assignDoctorsByIdToInstitution(Set<Long> doctorsIds, InstitutionPOJO institution) {
        Set<DoctorPOJO> doctors = resolveDoctors(doctorsIds);
        checkIfAllDoctorsWereAdded(doctorsIds, doctors);
        doctors.forEach(doctor -> doctor.getInstitutions()
                .add(institution));
        doctorRepository.saveAll(doctors);
        log.info("Successfully added doctors to institution {}", institution.getId());
        return new InstitutionDoctorsDto(institution.getName(),
                doctors.stream()
                        .map(doctorMapper::toSummaryDto)
                        .collect(Collectors.toSet()));
    }

    public Set<DoctorPOJO> showDoctorsByInstitution(Long institutionId) {
        return doctorRepository.findByInstitutionsId(institutionId)
                .stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toSet());
    }

    private void checkIfAllDoctorsWereAdded(Set<Long> doctorId, Set<DoctorPOJO> doctors) {
        Set<Long> resolvedIds = doctors.stream()
                .map(DoctorPOJO::getId)
                .collect(Collectors.toSet());
        for (Long id : doctorId) {
            if (!resolvedIds.contains(id)) {
                log.warn("doctor with Id {} not found while adding doctors to institution",id);
                throw new DoctorNotFoundException(id);
            }
        }
    }
    private void checkIfAllInstitutionsWereAdded(Set<Long> institutionsId,Set<InstitutionPOJO> institutions){
        Set<Long>resolvedId = institutions.stream().map(InstitutionPOJO::getId).collect(Collectors.toSet());
        for (Long id : institutionsId){
            if(!resolvedId.contains(id)){
                throw new InstitutionNotFoundException(id);
            }
        }
    }
}
