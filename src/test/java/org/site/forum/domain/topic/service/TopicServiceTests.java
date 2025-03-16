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
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@ExtendWith(MockitoExtension.class)
class TopicServiceTests {

    @Mock
    private TopicDao topicDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private FileMapper fileMapper;

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
    private File file;
    private TopicRequestDto topicRequestDto;
    private TopicResponseDto topicResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .build();

        file = File.builder()
                .id(UUID.randomUUID())
                .minioObjectName("test-file.txt")
                .contentType("text/plain")
                .topic(topic)
                .build();

        topicRequestDto = TopicRequestDto.builder()
                .title(TITLE)
                .content(CONTENT)
                .build();

        topic = Topic.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();

        topicResponseDto = TopicResponseDto.builder()
                .title(TITLE)
                .content(CONTENT)
                .authorId(user.getId())
                .files(fileMapper.toDto(List.of(file)))
                .build();
    }

    @Test
    void testCreateTopic() {
        List<MultipartFile> files = Collections.emptyList();

        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(topic);
        when(topicDao.saveTopic(topic)).thenReturn(topic);

        when(fileDao.findFilesByTopicId(topic.getId())).thenReturn(List.of(file));
        when(topicMapper.toDto(topic, List.of(file))).thenReturn(topicResponseDto);

        TopicResponseDto response = topicService.saveTopic(topicRequestDto, files);

        assertNotNull(response);
        assertEquals(topicResponseDto, response);

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(topicMapper).toEntity(topicRequestDto, user);
        verify(topicDao).saveTopic(topic);
        verify(topicMapper).toDto(topic, List.of(file));
    }

    @Test
    void testCreateTopicIfUserIsNull() {
        doThrow(new UserNotFoundException("User not found")).when(authenticationService).getAuthenticatedAndPersistedUser();

        Exception exception = assertThrows(UserNotFoundException.class, () -> topicService.saveTopic(topicRequestDto, List.of(multipartFile)));
        assertEquals("User not found", exception.getMessage());

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(userService, never()).saveUser(any());
        verify(topicMapper, never()).toEntity(any(), any());
        verify(topicDao, never()).saveTopic(any());
    }

    @Test
    void testGetTopic(){
        List<File> files = List.of(file);

        when(topicDao.getTopic(UUID.fromString(UUID_CONSTANT))).thenReturn(topic);
        when(fileDao.findFilesByTopicId(UUID.fromString(UUID_CONSTANT))).thenReturn(files);
        when(topicMapper.toDto(topic, files)).thenReturn(topicResponseDto);

        TopicResponseDto response = topicService.getTopic(UUID.fromString(UUID_CONSTANT));

        assertNotNull(response);
        assertEquals(topicResponseDto, response);

        verify(topicDao).getTopic(UUID.fromString(UUID_CONSTANT));
        verify(fileDao).findFilesByTopicId(UUID.fromString(UUID_CONSTANT));
        verify(topicMapper).toDto(topic, List.of(file));
    }

    @Test
    void testGetTopicIfTopicIdIsNull(){
        doThrow(new InvalidTopicIdException("Topic ID cannot be null")).when(topicDataIntegrity).validateTopicId(null);

        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicService.getTopic(null));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteTopic(){
        when(topicDao.getTopic(UUID.fromString(UUID_CONSTANT))).thenReturn(topic);

        topicService.deleteTopic(UUID.fromString(UUID_CONSTANT));

        verify(topicDao).deleteTopic(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void testDeleteTopicIfTopicNotFound(){
        when(topicDao.getTopic(UUID.fromString(UUID_CONSTANT))).thenReturn(null);

        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicService.deleteTopic(UUID.fromString(UUID_CONSTANT)));
        assertEquals("Topic not found", exception.getMessage());

        verify(topicDao).getTopic(UUID.fromString(UUID_CONSTANT));
        verify(topicDao, never()).deleteTopic(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void testDeleteTopicIfTopicIdIsNull(){
        doThrow(new InvalidTopicIdException("Topic ID cannot be null")).when(topicDataIntegrity).validateTopicId(null);

        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicService.deleteTopic(null));
        assertEquals("Topic ID cannot be null", exception.getMessage());

        verify(topicDao, never()).getTopic(any());
        verify(topicDao, never()).deleteTopic(any());
    }

}
