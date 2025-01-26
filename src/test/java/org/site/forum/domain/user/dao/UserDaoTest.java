package org.site.forum.domain.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDaoImpl userDao;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
    }

    @Test
    void saveUser_WhenUserDoesNotExist_SavesUserSuccessfully() {
        when(userRepository.existsById(userId)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userDao.saveUser(user);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_WhenUserAlreadyExists_ThrowsUserAlreadyExistsException() {
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userDao.saveUser(user));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).save(user);
    }

    @Test
    void getUserById_WhenIdIsValidAndUserExists_ReturnsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userDao.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenIdIsValidAndUserDoesNotExist_ReturnsEmptyOptional() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userDao.getUserById(userId);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenIdIsNull_ThrowsInvalidUserIdException() {
        assertThrows(InvalidUserIdException.class, () -> userDao.getUserById(null));
        verify(userRepository, never()).findById(any());
    }

}