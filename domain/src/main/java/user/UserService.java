package user;

import internalCommandsAndDtos.ChangePasswordInternalCommand;
import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import internalCommandsAndDtos.user.CreateUserInternalCommand;
import models.UserPOJO;

public interface UserService {
    PageResult<UserPOJO> findAll(PageQuery pageQuery);

    UserPOJO create(CreateUserInternalCommand command);

    UserPOJO findById(Long id);

    void changePassword(Long id, ChangePasswordInternalCommand command);

    void deleteUser(Long id);
}
