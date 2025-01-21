package org.site.forum.domain.topic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.user.entity.User;
import org.site.forum.domain.user.service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.TOPIC_CONTENT;
import static org.site.forum.constants.TestConstants.TOPIC_TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTests {

    @Mock
    private TopicDao topicDao;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TopicServiceImpl topicService;

    private User user;
    private Topic topic;
    private TopicRequestDto topicRequestDto;
    private TopicResponseDto topicResponseDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .uuid(UUID.fromString(UUID_CONSTANT))
                .build();

        topicRequestDto = TopicRequestDto.builder()
                .title(TOPIC_TITLE)
                .content(TOPIC_CONTENT)
                .build();

        topic = Topic.builder()
                .title(TOPIC_TITLE)
                .content(TOPIC_CONTENT)
                .author(user)
                .build();

        topicResponseDto = TopicResponseDto.builder()
                .title(TOPIC_TITLE)
                .content(TOPIC_CONTENT)
                .author(user)
                .build();
    }

    @Test
    public void testCreateTopic(){
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicMapper.topicBuilder(topicRequestDto, user)).thenReturn(topic);
        when(topicDao.saveTopic(topic)).thenReturn(topic);
        when(topicMapper.toDto(topic)).thenReturn(topicResponseDto);

        TopicResponseDto response = topicService.createTopic(topicRequestDto);

        assertNotNull(response);
        assertEquals(topicResponseDto, response);

        verify(authenticationService).getAuthenticatedUser();
        verify(userService).saveUser(user);
        verify(topicMapper).topicBuilder(topicRequestDto, user);
        verify(topicDao).saveTopic(topic);
        verify(topicMapper).toDto(topic);
    }

    @Test
    public void testCreateTopicIfUserIsNull() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> topicService.createTopic(topicRequestDto));
        assertEquals("User not found", exception.getMessage());

        verify(authenticationService).getAuthenticatedUser();
        verify(userService, never()).saveUser(any());
        verify(topicMapper, never()).topicBuilder(any(), any());
        verify(topicDao, never()).saveTopic(any());
    }

    @Test
    public void testGetTopic(){
        when(topicDao.getTopic(1L)).thenReturn(topic);
        when(topicMapper.toDto(topic)).thenReturn(topicResponseDto);

        TopicResponseDto response = topicService.getTopic(1L);

        assertNotNull(response);
        assertEquals(topicResponseDto, response);

        verify(topicDao).getTopic(1L);
        verify(topicMapper).toDto(topic);
    }

}
