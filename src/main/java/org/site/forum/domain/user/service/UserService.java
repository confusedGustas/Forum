package org.site.forum.domain.user.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;

    public void saveUser(User user) {
        userDao.saveUser(user);
    }
}
