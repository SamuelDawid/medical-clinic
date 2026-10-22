package user;

import exceptions.InvalidEmailException;
import exceptions.InvalidPasswordException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import internalCommandsAndDtos.ChangePasswordInternalCommand;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.user.CreateUserInternalCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.UserPOJO;
import validators.EmailValidator;

@Slf4j
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepositoryPort userRepository;

    private final EmailValidator emailValidator;

    public PageResult<UserPOJO> findAll(PageQuery pageQuery) {
        return userRepository.findAll(pageQuery);
    }

    public UserPOJO create(@NonNull CreateUserInternalCommand command) {
        log.info("Creating new user");
        String emailNormalized = EmailValidator.normalize(command.email());
        validateEmail(emailNormalized);
        validatePassword(command.password());
        UserPOJO user = UserPOJO.create(command);
        user.setEmail(emailNormalized);
        UserPOJO saved = userRepository.save(user);
        log.info("User with Id {} created successfully", saved.getId());
        return saved;
    }

    public UserPOJO findById(@NonNull Long id) {
        return getUserOrThrow(id);
    }

    public void changePassword(@NonNull Long id, @NonNull ChangePasswordInternalCommand command) {
        log.info("Changing password for User with Id {}", id);
        validatePassword(command.newPassword());
        UserPOJO user = getUserOrThrow(id);
        user.setPassword(command.newPassword());
        userRepository.save(user);
        log.info("Password changed for User {}", id);
    }

    public void deleteUser(@NonNull Long id) {
        log.info("Deleting user with Id {}", id);
        getUserOrThrow(id);
        userRepository.delete(id);
        log.info("User with Id {} removed", id);
    }

    private void validateEmail(String email) {
        if (!emailValidator.validate(email)) {
            log.warn("email : {} not a valid email",email);
            throw new InvalidEmailException(email);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("user with email {} already exists",email);
            throw new UserAlreadyExistsException();
        }
    }

    private void validatePassword(String password) {
        if (password.isBlank()) {
            log.warn("blank or null password");
            throw new InvalidPasswordException();
        }
        if(password.length() < 5){
            log.warn("password too short");
            throw new InvalidPasswordException();
        }
    }
    private UserPOJO getUserOrThrow(Long id){
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("deleting user with id {} failed, user not found",id);
            return new UserNotFoundException();
        });
    }
}
