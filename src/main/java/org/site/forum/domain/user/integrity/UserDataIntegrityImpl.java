package org.site.forum.domain.user.integrity;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDataIntegrityImpl implements UserDataIntegrity {

    public static final String USER_CANNOT_BE_NULL = "User cannot be null";
    public static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null";
    private static final String USER_ALREADY_EXISTS = "User with the specified UUID already exists";

    private final UserDao userDao;

    @Override
    public void validateUser(User user) {
        if (user == null) {
            throw new InvalidUserException(USER_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidUserIdException(USER_ID_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateUserNotNull(User user) {
        if (user == null) {
            throw new InvalidUserException(USER_CANNOT_BE_NULL);
        }
    }

    public void validateUserDoesNotExist(User user) {
        if (userDao.getUserById(user.getId()).isPresent()) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }
    }

}