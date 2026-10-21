package models;

import internalCommands.user.CreateUserInternalCommand;
import internalCommands.user.PatchUserInternalCommand;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
public class UserPOJO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @ToString.Exclude
    private String password;

    public static UserPOJO create(CreateUserInternalCommand command) {
        return UserPOJO.builder()
                .id(null)
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .password(command.password())
                .build();
    }
    public void update(PatchUserInternalCommand command){
        if(Objects.nonNull(command.firstName())){
            this.firstName = command.firstName();
        }
        if(Objects.nonNull(command.lastName())){
            this.lastName = command.lastName();
        }
        if(Objects.nonNull(command.email())){
            this.email = command.email();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPOJO other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
