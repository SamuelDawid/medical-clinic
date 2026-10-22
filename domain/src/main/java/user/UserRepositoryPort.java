package user;

import internalCommandsAndDtos.PageQuery;
import internalCommandsAndDtos.PageResult;
import models.UserPOJO;

import java.util.Optional;

public interface UserRepositoryPort {
    PageResult<UserPOJO> findAll(PageQuery pageQuery);
    UserPOJO save(UserPOJO user);
    Optional<UserPOJO> findById(Long id);
    boolean existsByEmail(String email);
    void delete(Long id);
}
