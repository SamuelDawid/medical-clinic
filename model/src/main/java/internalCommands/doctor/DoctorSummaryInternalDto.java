package internalCommands.doctor;

public record DoctorSummaryInternalDto(
        String firstName,
        String lastName,
        String medicalSpecialty
) {
}
