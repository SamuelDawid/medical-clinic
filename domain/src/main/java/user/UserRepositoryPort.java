package user;

import internalCommands.PageQuery;
import internalCommands.PageResult;
import models.UserPOJO;

import java.util.Optional;

public interface UserRepositoryPort {
    PageResult<UserPOJO> findAll(PageQuery pageQuery);
    UserPOJO save(UserPOJO user);
    Optional<UserPOJO> findById(Long id);
    boolean existsByEmail(String email);
    void delete(Long id);
}
