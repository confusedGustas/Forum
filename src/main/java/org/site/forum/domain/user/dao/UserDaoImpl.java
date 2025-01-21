package org.site.forum.domain.user.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        checkIfUserExistsByUuid(user);

        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    private void checkIfUserExistsByUuid(User user) {
        if (userRepository.existsByUuid(user.getUuid())) {
            throw new IllegalArgumentException("User with the specified UUID already exists");
        }
    }


}
