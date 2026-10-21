package user;

import internalCommands.PageQuery;
import internalCommands.PageResult;
import internalCommands.user.CreateUserInternalCommand;
import models.UserPOJO;

public interface UserService {
    PageResult<UserPOJO> findAll(PageQuery pageQuery);

    UserPOJO create(CreateUserInternalCommand command);

    UserPOJO findById(Long id);
}
