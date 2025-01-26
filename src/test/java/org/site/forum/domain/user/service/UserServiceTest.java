package org.site.forum.domain.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.UserAlreadyExistsException;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.integrity.UserDataIntegrity;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserDataIntegrity userDataIntegrity;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveUser_NullUser_ThrowsInvalidUserException() {
        doThrow(new InvalidUserException("")).when(userDataIntegrity).validateUserNotNull(null);
        assertThrows(InvalidUserException.class, () -> userService.saveUser(null));
        verify(userDao, never()).saveUser(any());
    }

    @Test
    void saveUser_ExistingUser_ThrowsUserAlreadyExistsException() {
        User user = new User(UUID.randomUUID());
        doNothing().when(userDataIntegrity).validateUserNotNull(user);
        doThrow(new UserAlreadyExistsException("")).when(userDataIntegrity).validateUserDoesNotExist(user);

        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(user));
        verify(userDao, never()).saveUser(any());
    }

    @Test
    void saveUser_ValidUser_SavesToDao() {
        User user = new User(UUID.randomUUID());
        doNothing().when(userDataIntegrity).validateUserNotNull(user);
        doNothing().when(userDataIntegrity).validateUserDoesNotExist(user);

        userService.saveUser(user);

        verify(userDao).saveUser(user);
    }

    @Test
    void saveUser_ValidationOrder_ChecksNotNullFirst() {
        User user = new User(UUID.randomUUID());
        doThrow(new InvalidUserException("")).when(userDataIntegrity).validateUserNotNull(user);

        assertThrows(InvalidUserException.class, () -> userService.saveUser(user));
        verify(userDataIntegrity, never()).validateUserDoesNotExist(any());
    }

}