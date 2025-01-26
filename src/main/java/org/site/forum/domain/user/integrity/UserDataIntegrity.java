package org.site.forum.domain.user.integrity;

import org.site.forum.domain.user.entity.User;

public interface UserDataIntegrity {

    void validateUserNotNull(User user);
    void validateUserDoesNotExist(User user);

}
