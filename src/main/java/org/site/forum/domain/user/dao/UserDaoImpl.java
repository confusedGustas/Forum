package org.site.forum.domain.user.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null";
    private static final String USER_ALREADY_EXISTS = "User with the specified UUID already exists";

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        checkIfUserExistsByUuid(user);
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        if (id == null) {
            throw new InvalidUserIdException(USER_ID_CANNOT_BE_NULL);
        }

        return userRepository.findById(id);
    }

    private void checkIfUserExistsByUuid(User user) {
        if (userRepository.existsById(user.getId())) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }
    }

}