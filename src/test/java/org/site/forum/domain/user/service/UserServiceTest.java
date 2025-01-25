package org.site.forum.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
    }

    @Test
    void saveUser_WhenUserIsNull_ThrowsInvalidUserException() {
        assertThrows(InvalidUserException.class, () -> userService.saveUser(null));
        verify(userDao, never()).getUserById(any());
        verify(userDao, never()).saveUser(any());
    }

    @Test
    void saveUser_WhenUserAlreadyExists_ThrowsUserAlreadyExistsException() {
        when(userDao.getUserById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(user));
        verify(userDao, times(1)).getUserById(userId);
        verify(userDao, never()).saveUser(any());
    }

    @Test
    void saveUser_WhenUserDoesNotExist_SavesUserSuccessfully() {
        when(userDao.getUserById(userId)).thenReturn(Optional.empty());

        userService.saveUser(user);

        verify(userDao, times(1)).getUserById(userId);
        verify(userDao, times(1)).saveUser(user);
    }

}