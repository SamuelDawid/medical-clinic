package internalCommandsAndDtos.user;

public record CreateUserInternalCommand(
         String firstName,
         String lastName,
         String email,
         String password
) {
}
