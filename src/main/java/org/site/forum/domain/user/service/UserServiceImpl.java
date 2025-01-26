package org.site.forum.domain.user.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.integrity.UserDataIntegrity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserDataIntegrity userDataIntegrity;

    @Override
    public void saveUser(User user) {
        userDataIntegrity.validateUserNotNull(user);
        userDataIntegrity.validateUserDoesNotExist(user);

        userDao.saveUser(user);
    }

}