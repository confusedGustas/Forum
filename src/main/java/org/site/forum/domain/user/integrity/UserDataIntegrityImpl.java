package org.site.forum.domain.user.integrity;

import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDataIntegrityImpl implements UserDataIntegrity {

    public static final String USER_CANNOT_BE_NULL = "User cannot be null";
    public static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null";

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


}
