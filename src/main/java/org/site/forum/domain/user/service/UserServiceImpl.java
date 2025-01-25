package org.site.forum.domain.user.service;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_CANNOT_BE_NULL = "User cannot be null";
    private static final String USER_ALREADY_EXISTS = "User with the specified UUID already exists";

    private final UserDao userDao;

    @Override
    public void saveUser(User user) {
        if (user == null) {
            throw new InvalidUserException(USER_CANNOT_BE_NULL);
        }

        if (userDao.getUserById(user.getId()).isPresent()) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        userDao.saveUser(user);
    }

}