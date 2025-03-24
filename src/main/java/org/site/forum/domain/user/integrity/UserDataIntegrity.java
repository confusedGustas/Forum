package org.site.forum.domain.user.integrity;

import org.site.forum.domain.user.entity.User;
import java.util.UUID;

public interface UserDataIntegrity {

    void validateUser(User user);
    void validateUserId(UUID userId);
    void validateUserNotNull(User user);
    void validateUserDoesNotExist(User user);

}
