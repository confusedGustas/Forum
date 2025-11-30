package org.site.forum.domain.topic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.FileService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.request.TopicRequestDto;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.entity.User;
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
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;
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
    private TopicDataIntegrity topicDataIntegrity;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TopicServiceImpl topicService;

    private User user;
    private Topic topic;
    private TopicRequestDto topicRequestDto;
    private UUID testTopicId;

    @BeforeEach
    void setUp() {
        testTopicId = UUID.randomUUID();
        user = User.builder().id(UUID.randomUUID()).build();
        topicRequestDto = TopicRequestDto.builder().title("Test Title").content("Test Content").build();

        topic = Topic.builder()
                .id(testTopicId)
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .build();

        topicRequestDto = TopicRequestDto.builder()
                .title("Test Title")
                .content("Test Content")
                .build();
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
    void testGetTopicIfTopicIdIsNull(){
        doThrow(new InvalidTopicIdException("Topic ID cannot be null")).when(topicDataIntegrity).validateTopicId(null);

        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicService.getTopic(null));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteTopic(){
        when(topicDao.getTopic(UUID.fromString(UUID_CONSTANT))).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(authenticationService.isAdmin()).thenReturn(true);

        topicService.deleteTopic(UUID.fromString(UUID_CONSTANT));

        verify(topicDao).deleteTopic(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void shouldThrow_whenDeleteTopic_userIsNotOwnerAndNotAdmin() {
        UUID topicId = UUID.fromString(UUID_CONSTANT);
        User authenticatedUser = User.builder().id(UUID.randomUUID()).build();
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticationService.isAdmin()).thenReturn(false);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> topicService.deleteTopic(topicId)
        );

        assertEquals("You are not authorized to delete this topic", exception.getMessage());
        verify(topicDao, never()).deleteTopic(any());
    }

    @Test
    void shouldThrow_whenDeleteTopicNotFound() {
        UUID topicId = UUID.fromString(UUID_CONSTANT);
        when(topicDao.getTopic(topicId)).thenReturn(null);

        InvalidTopicIdException exception = assertThrows(
                InvalidTopicIdException.class,
                () -> topicService.deleteTopic(topicId)
        );

        assertEquals("Topic not found", exception.getMessage());

        verify(topicDao).getTopic(topicId);
        verify(topicDao, never()).deleteTopic(topicId);
    }

    @Test
    void testDeleteTopicIfTopicIdIsNull(){
        doThrow(new InvalidTopicIdException("Topic ID cannot be null")).when(topicDataIntegrity).validateTopicId(null);

        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicService.deleteTopic(null));
        assertEquals("Topic ID cannot be null", exception.getMessage());

        verify(topicDao, never()).getTopic(any());
        verify(topicDao, never()).deleteTopic(any());
    }

    @Test
    void testDeleteTopicWhenTopicNotFound() {
        UUID topicId = UUID.randomUUID();
        when(topicDao.getTopic(topicId)).thenThrow(new InvalidTopicIdException("Not found"));
        assertThrows(InvalidTopicIdException.class, () -> topicService.deleteTopic(topicId));
    }

    @Test
    void testGetTopicWithNullId() {
        doThrow(new InvalidTopicIdException("Test")).when(topicDataIntegrity).validateTopicId(null);
        assertThrows(InvalidTopicIdException.class, () -> topicService.getTopic(null));
    }

    @Test
    void testUpdateTopicWithFiles() {
        UUID topicId = testTopicId;
        Topic updatedTopic = Topic.builder()
                .id(topicId)
                .title("Updated Title")
                .content("Updated Content")
                .author(user)
                .build();
        TopicResponseDto expectedDto = new TopicResponseDto();
        List<MultipartFile> files = List.of(multipartFile);

        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(updatedTopic);
        when(topicDao.updateTopic(topicId, updatedTopic)).thenReturn(updatedTopic);
        when(fileDao.findFilesByTopicId(topicId)).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(updatedTopic, Collections.emptyList())).thenReturn(expectedDto);
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        doNothing().when(topicDataIntegrity).validateFileCount(topicId);

        TopicResponseDto result = topicService.updateTopic(topicId, topicRequestDto, files);

        assertSame(expectedDto, result);
        verify(fileService).uploadFiles(files, updatedTopic);
    }

    @Test
    void testUpdateTopic() {
        UUID topicId = testTopicId;
        Topic updatedTopic = Topic.builder()
                .id(topicId)
                .title("Updated Title")
                .content("Updated Content")
                .author(user)
                .build();
        TopicResponseDto expectedDto = new TopicResponseDto();

        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(updatedTopic);
        when(topicDao.updateTopic(topicId, updatedTopic)).thenReturn(updatedTopic);
        when(fileDao.findFilesByTopicId(topicId)).thenReturn(Collections.emptyList());
        when(topicMapper.toDto(updatedTopic, Collections.emptyList())).thenReturn(expectedDto);
        doNothing().when(topicDataIntegrity).validateFileCount(topicId);

        TopicResponseDto result = topicService.updateTopic(topicId, topicRequestDto, Collections.emptyList());

        assertSame(expectedDto, result);
        verify(topicDataIntegrity).validateTopicId(topicId);
        verify(topicDataIntegrity).validateTopicRequestDto(topicRequestDto);
        verify(fileService, never()).uploadFiles(any(), any());
    }

    @Test
    void shouldThrow_whenUpdateTopicWithInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicMapper.toEntity(topicRequestDto, user)).thenReturn(topic);
        when(topicDao.updateTopic(invalidId, topic)).thenThrow(new InvalidTopicIdException("Not found"));

        InvalidTopicIdException exception = assertThrows(
                InvalidTopicIdException.class,
                () -> topicService.updateTopic(invalidId, topicRequestDto, Collections.emptyList())
        );

        assertEquals("Not found", exception.getMessage());
    }

}