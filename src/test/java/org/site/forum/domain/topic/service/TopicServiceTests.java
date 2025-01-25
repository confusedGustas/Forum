package org.site.forum.domain.topic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.file.mapper.FileMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTests {

    @Mock
    private TopicDao topicDao;

    @Mock
    private FileDao fileDao;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private FileMapper fileMapper;

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
    public void setUp() {
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
                .author(user)
                .files(fileMapper.toDto(List.of(file)))
                .build();
    }

    @Test
    public void testCreateTopic() {
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
        verify(fileService, never()).uploadFiles(files, topic);
        verify(topicMapper).toDto(topic, List.of(file));
    }

    @Test
    void testCreateTopicIfUserIsNull() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(null);

        Exception exception = assertThrows(UserNotFoundException.class, () -> topicService.saveTopic(topicRequestDto, List.of(multipartFile)));
        assertEquals("User not found", exception.getMessage());

        verify(authenticationService).getAuthenticatedUser();
        verify(userService, never()).saveUser(any());
        verify(topicMapper, never()).topicBuilder(any(), any());
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

}
