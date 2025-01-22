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

    private final UserDao userDao;

    @Override
    public void saveUser(User user) {
        if (user == null) {
            throw new InvalidUserException("User cannot be null");
        }

        if (userDao.getUserById(user.getId()).isPresent()) {
            throw new UserAlreadyExistsException("User with the specified UUID already exists");
        }

        userDao.saveUser(user);
    }
}