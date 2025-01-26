package org.site.forum.config.auth;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.mapper.UserMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserMapper userMapper;
    private final UserDao userDao;

    private static final String USER_NOT_FOUND = "User not found";

    public User getAuthenticatedUser() {
        return userMapper.toUser((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public User getAuthenticatedAndPersistedUser() {
        User user = getAuthenticatedUser();
        checkUser(user);

        if (userDao.getUserById(user.getId()).isEmpty()) {
            userDao.saveUser(user);
        }

        return user;
    }

    private void checkUser(User user) {
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }
}
