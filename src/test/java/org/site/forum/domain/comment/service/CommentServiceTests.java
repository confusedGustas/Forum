package org.site.forum.domain.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.CREATED_AT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TopicDao topicDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Topic topic;
    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private ParentCommentResponseDto parentCommentResponseDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .build();

        topic = Topic.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();

        comment = Comment.builder()
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(null)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text(CONTENT)
                .topicId(UUID.fromString(UUID_CONSTANT))
                .parentCommentId(null)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .author(user)
                .topic(topic)
                .parentComment(null)
                .build();

        parentCommentResponseDto = ParentCommentResponseDto.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    public void testSaveRootComment() {
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicDao.getTopic(commentRequestDto.getTopicId())).thenReturn(topic);
        when(commentMapper.toEntity(commentRequestDto, user, topic, null)).thenReturn(comment);
        when(commentDao.saveComment(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.saveComment(commentRequestDto);

        assertNotNull(result);
        assertNull(result.getParentComment());
        assertEquals(commentResponseDto, result);

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(topicDao).getTopic(commentRequestDto.getTopicId());
        verify(commentMapper).toEntity(commentRequestDto, user, topic, null);
        verify(commentDao).saveComment(comment);
        verify(commentMapper).toDto(comment);

    }

    @Test
    public void testSaveReply() {
        UUID parentCommentId = UUID.randomUUID();
        commentRequestDto.setParentCommentId(parentCommentId);
        commentResponseDto.setParentComment(parentCommentResponseDto);

        var reply = Comment.builder()
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(comment)
                .build();

        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicDao.getTopic(commentRequestDto.getTopicId())).thenReturn(topic);
        when(commentDao.getComment(commentRequestDto.getParentCommentId())).thenReturn(comment);
        when(commentMapper.toEntity(commentRequestDto, user, topic, comment)).thenReturn(reply);
        when(commentDao.saveComment(reply)).thenReturn(reply);
        when(commentMapper.toDto(reply)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.saveComment(commentRequestDto);

        assertNotNull(result);
        assertEquals(commentResponseDto, result);
        assertEquals(parentCommentResponseDto, commentResponseDto.getParentComment());

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(topicDao).getTopic(commentRequestDto.getTopicId());
        verify(commentDao).getComment(commentRequestDto.getParentCommentId());
        verify(commentMapper).toEntity(commentRequestDto, user, topic, comment);
        verify(commentDao).saveComment(reply);
        verify(commentMapper).toDto(reply);
    }

    @Test
    public void testGetComment() {
        when(commentDao.getComment(UUID.fromString(UUID_CONSTANT))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.getComment(UUID.fromString(UUID_CONSTANT));

        assertNotNull(result);
        assertEquals(commentResponseDto, result);

        verify(commentDao).getComment(UUID.fromString(UUID_CONSTANT));
        verify(commentMapper).toDto(comment);
    }

}
