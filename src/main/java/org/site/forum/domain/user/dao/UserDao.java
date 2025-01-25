package org.site.forum.domain.user.dao;

import org.site.forum.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    User saveUser(User user);
    Optional<User> getUserById(UUID id);

}
