package org.site.forum.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidUserException;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dto.UserResponseDto;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.integrity.UserDataIntegrity;
import org.site.forum.domain.user.mapper.UserMapper;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.CREATED_AT;
import static org.site.forum.constants.TestConstants.TITLE;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserDataIntegrity userDataIntegrity;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CommentDao commentDao;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TopicDao topicDao;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private FileDao fileDao;

    private User user;
    private UUID userId;
    private ParentCommentResponseDto parentCommentResponseDto;
    private UserResponseDto userResponseDto;
    private TopicResponseDto topicResponseDto;
    private Topic topic;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        comment = Comment.builder()
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(null)
                .build();

        topic = Topic.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();

        parentCommentResponseDto = ParentCommentResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .authorId(user.getId())
                .build();

        topicResponseDto = TopicResponseDto.builder()
                .id(UUID.randomUUID())
                .title(TITLE)
                .content(CONTENT)
                .authorId(user.getId())
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .deletedAt(null)
                .rating(0)
                .files(List.of())
                .updatedAt(null)
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(userId)
                .topics(List.of())
                .comments(List.of())
                .build();
    }

    void saveUser_NullUser_ThrowsInvalidUserException() {
        doThrow(new InvalidUserException("")).when(userDataIntegrity).validateUserNotNull(null);
        assertThrows(InvalidUserException.class, () -> userService.saveUser(null));
        verify(userDao, never()).saveUser(any());
    }

    @Test
    void getAuthenticatedUserComments() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(commentDao.getAllCommentsByUserId(user.getId(), pageable)).thenReturn(new PageImpl<>(List.of(comment)));
        when(commentMapper.toParentCommentDto(comment)).thenReturn(parentCommentResponseDto);

        Page<ParentCommentResponseDto> result = userService.getAuthenticatedUserComments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(parentCommentResponseDto, result.getContent().get(0));

        verify(commentDao).getAllCommentsByUserId(user.getId(), pageable);
        verify(commentMapper).toParentCommentDto(comment);
    }

    @Test
    void getAuthenticatedUserComments_WhenUserIsNull_ThrowsInvalidUserException() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(InvalidUserException.class).when(authenticationService).getAuthenticatedUser();

        assertThrows(InvalidUserException.class, () -> userService.getAuthenticatedUserComments(pageable));

        verify(commentDao, never()).getAllCommentsByUserId(any(), any());
        verify(commentMapper, never()).toParentCommentDto(any());
    }

    @Test
    void getAuthenticatedUserTopics() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicDao.getAllTopicsByUserId(user.getId(), pageable)).thenReturn(new PageImpl<>(List.of(topic)));
        when(topicMapper.toDto(topic, List.of())).thenReturn(topicResponseDto);

        Page<TopicResponseDto> result = userService.getAuthenticatedUserTopics(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(topicResponseDto, result.getContent().get(0));

        verify(topicDao).getAllTopicsByUserId(user.getId(), pageable);
        verify(topicMapper).toDto(topic, List.of());
    }

    @Test
    void getAuthenticatedUserTopics_WhenUserIsNull_ThrowsInvalidUserException() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(InvalidUserException.class).when(authenticationService).getAuthenticatedUser();

        assertThrows(InvalidUserException.class, () -> userService.getAuthenticatedUserTopics(pageable));

        verify(topicDao, never()).getAllTopicsByUserId(any(), any());
        verify(topicMapper, never()).toDto(any(), any());
    }

    @Test
    void getUserById() {
        when(userDao.getUserById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());

        verify(userDao).getUserById(userId);
        verify(userMapper).toUserResponseDto(user);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ThrowsInvalidUserException() {
        when(userDao.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> userService.getUserById(userId));

        verify(userDao).getUserById(userId);
        verify(userMapper, never()).toUserResponseDto(any());
    }

    @Test
    void testGetUserComments() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(commentDao.getAllCommentsByUserId(userId, pageable)).thenReturn(new PageImpl<>(List.of(comment)));
        when(commentMapper.toParentCommentDto(comment)).thenReturn(parentCommentResponseDto);

        Page<ParentCommentResponseDto> result = userService.getUserComments(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(parentCommentResponseDto, result.getContent().get(0));

        verify(commentDao).getAllCommentsByUserId(userId, pageable);
        verify(commentMapper).toParentCommentDto(comment);
    }

    @Test
    void testGetUserComments_WhenUserDoesNotExist_ThrowsInvalidUserIdException() {
        PageRequest pageable = PageRequest.of(0, 10);
        UUID nonExistentUserId = UUID.randomUUID();

        when(commentDao.getAllCommentsByUserId(nonExistentUserId, pageable))
                .thenThrow(new InvalidUserIdException("User not found"));

        assertThrows(InvalidUserIdException.class, () -> userService.getUserComments(nonExistentUserId, pageable));

        verify(commentDao).getAllCommentsByUserId(nonExistentUserId, pageable);
        verify(commentMapper, never()).toParentCommentDto(any());
    }

    @Test
    void testGetUserTopics() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(topicDao.getAllTopicsByUserId(userId, pageable)).thenReturn(new PageImpl<>(List.of(topic)));
        when(topicMapper.toDto(topic, List.of())).thenReturn(topicResponseDto);

        Page<TopicResponseDto> result = userService.getUserTopics(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(topicResponseDto, result.getContent().get(0));

        verify(topicDao).getAllTopicsByUserId(userId, pageable);
        verify(topicMapper).toDto(topic, List.of());
    }

    @Test
    void testGetUserTopics_WhenUserDoesNotExist_ThrowsInvalidUserIdException() {
        PageRequest pageable = PageRequest.of(0, 10);
        UUID nonExistentUserId = UUID.randomUUID();

        when(topicDao.getAllTopicsByUserId(nonExistentUserId, pageable))
                .thenThrow(new InvalidUserIdException("User not found"));

        assertThrows(InvalidUserIdException.class, () -> userService.getUserTopics(nonExistentUserId, pageable));

        verify(topicDao).getAllTopicsByUserId(nonExistentUserId, pageable);
        verify(topicMapper, never()).toDto(any(), any());
    }

}