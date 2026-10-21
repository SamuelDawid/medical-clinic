package internalCommands.user;

public record PatchUserInternalCommand(
        String firstName,
        String lastName,
        String email
) {
}
