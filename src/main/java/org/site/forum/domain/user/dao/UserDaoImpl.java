package org.site.forum.domain.user.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.user.UserRepository;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }


}
