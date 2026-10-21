package errorMessage;

public record ErrorMessageDto(String message,
                              int status,
                              String timeOfError
) {
}
