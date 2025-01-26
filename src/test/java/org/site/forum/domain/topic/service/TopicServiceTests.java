package org.site.forum.domain.topic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.FileService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTests {

    @Mock
    private TopicDao topicDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private FileService fileService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private TopicDataIntegrity topicDataIntegrity;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TopicServiceImpl topicService;

    private User user;
    private Topic topic;
    private TopicRequestDto topicRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).build();
        topic = Topic.builder().title("Test Title").content("Test Content").author(user).build();
        topicRequestDto = TopicRequestDto.builder().title("Test Title").content("Test Content").build();
    }

    @Test
    void testCreateTopic() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(topic);
        when(topicDao.saveTopic(topic)).thenReturn(topic);
        when(fileDao.findFilesByTopicId(topic.getId())).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(topic, Collections.emptyList())).thenReturn(new TopicResponseDto());

        TopicResponseDto response = topicService.saveTopic(topicRequestDto, Collections.emptyList());

        assertNotNull(response);
        verify(fileDao).findFilesByTopicId(topic.getId());
    }

    @Test
    void testCreateTopicIfUserIsNull() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> topicService.saveTopic(topicRequestDto, List.of(multipartFile)));
        verify(topicDao, never()).saveTopic(any());
    }

    @Test
    void testGetTopic() {
        UUID topicId = UUID.randomUUID();
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(fileDao.findFilesByTopicId(topicId)).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(topic, Collections.emptyList())).thenReturn(new TopicResponseDto());

        TopicResponseDto response = topicService.getTopic(topicId);

        assertNotNull(response);
        verify(fileDao).findFilesByTopicId(topicId);
    }

    @Test
    void testDeleteTopic() {
        UUID topicId = UUID.randomUUID();
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        topicService.deleteTopic(topicId);
        verify(topicDao).deleteTopic(topicId);
    }

    @Test
    void testDeleteTopicWhenTopicNotFound() {
        UUID topicId = UUID.randomUUID();
        when(topicDao.getTopic(topicId)).thenThrow(new InvalidTopicIdException("Not found"));
        assertThrows(InvalidTopicIdException.class, () -> topicService.deleteTopic(topicId));
    }

    @Test
    void testCreateTopicWithFiles() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(topic);
        when(topicDao.saveTopic(topic)).thenReturn(topic);
        when(fileDao.findFilesByTopicId(topic.getId())).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(topic, Collections.emptyList())).thenReturn(new TopicResponseDto());

        topicService.saveTopic(topicRequestDto, List.of(multipartFile));

        verify(fileService).uploadFiles(List.of(multipartFile), topic);
    }

    @Test
    void testSaveUserIfNotPersisted() {
        User newUser = User.builder().id(UUID.randomUUID()).build();
        when(authenticationService.getAuthenticatedUser()).thenReturn(newUser);
        when(userDao.getUserById(newUser.getId())).thenReturn(Optional.empty());
        when(topicMapper.toEntity(topicRequestDto, newUser)).thenReturn(topic);
        when(topicDao.saveTopic(topic)).thenReturn(topic);
        when(fileDao.findFilesByTopicId(topic.getId())).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(topic, Collections.emptyList())).thenReturn(new TopicResponseDto());

        topicService.saveTopic(topicRequestDto, Collections.emptyList());

        verify(userService).saveUser(newUser);
    }

    @Test
    void testGetTopicWithNullId() {
        doThrow(new InvalidTopicIdException("Test")).when(topicDataIntegrity).validateTopicId(null);
        assertThrows(InvalidTopicIdException.class, () -> topicService.getTopic(null));
    }

}