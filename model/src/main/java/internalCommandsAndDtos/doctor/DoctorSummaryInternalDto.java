package internalCommandsAndDtos.doctor;

public record DoctorSummaryInternalDto(
        String firstName,
        String lastName,
        String medicalSpecialty
) {
}
