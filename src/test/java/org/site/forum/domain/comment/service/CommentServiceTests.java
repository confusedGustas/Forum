package org.site.forum.domain.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidCommentRequestException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.integrity.CommentDataIntegrity;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.CREATED_AT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TopicDao topicDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentDataIntegrity commentDataIntegrity;

    @Mock
    private TopicDataIntegrity topicDataIntegrity;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Topic topic;
    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private ReplyResponseDto replyResponseDto;
    private ParentCommentResponseDto parentCommentResponseDto;

    @BeforeEach
    void setUp() {
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

        parentCommentResponseDto = ParentCommentResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .authorId(user.getId())
                .topicId(topic.getId())
                .build();

        replyResponseDto = ReplyResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .userId(user.getId())
                .topicId(topic.getId())
                .parentCommentId(parentCommentResponseDto.getId())
                .replies(Collections.emptyList())
                .build();

    }

    @Test
    void testSaveRootComment() {
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(topicDao.getTopic(commentRequestDto.getTopicId())).thenReturn(topic);
        when(commentMapper.toEntity(commentRequestDto, user, topic, null)).thenReturn(comment);
        when(commentDao.saveComment(comment)).thenReturn(comment);
        when(commentMapper.toParentCommentDto(comment)).thenReturn(parentCommentResponseDto);

        ParentCommentResponseDto result = commentService.saveComment(commentRequestDto);

        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(topicDao).getTopic(commentRequestDto.getTopicId());
        verify(commentMapper).toEntity(commentRequestDto, user, topic, null);
        verify(commentDao).saveComment(comment);
        verify(commentMapper).toParentCommentDto(comment);
    }

    @Test
    void testSaveReply() {
        UUID parentCommentId = UUID.randomUUID();
        commentRequestDto.setParentCommentId(parentCommentId);
        replyResponseDto.setParentCommentId(parentCommentId);

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
        when(commentMapper.toParentCommentDto(reply)).thenReturn(parentCommentResponseDto);

        ParentCommentResponseDto result = commentService.saveComment(commentRequestDto);

        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);
        assertEquals(parentCommentId, replyResponseDto.getParentCommentId());

        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(topicDao).getTopic(commentRequestDto.getTopicId());
        verify(commentDao).getComment(commentRequestDto.getParentCommentId());
        verify(commentMapper).toEntity(commentRequestDto, user, topic, comment);
        verify(commentDao).saveComment(reply);
        verify(commentMapper).toParentCommentDto(reply);
    }

    @Test
    void testSaveCommentWhenTextIsNull() {
        commentRequestDto.setText(null);

        doThrow(new InvalidCommentRequestException("Comment text cannot be empty or null"))
                .when(commentDataIntegrity).validateCommentRequestDto(commentRequestDto);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text cannot be empty or null", exception.getMessage());
    }

    @Test
    void testSaveCommentWhenTextIsEmpty() {
        commentRequestDto.setText("");

        doThrow(new InvalidCommentRequestException("Comment text cannot be empty or null"))
                .when(commentDataIntegrity).validateCommentRequestDto(commentRequestDto);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text cannot be empty or null", exception.getMessage());
    }


    @Test
    void testGetCommentByParent() {
        when(commentDao.getComment(UUID.fromString(UUID_CONSTANT))).thenReturn(comment);
        when(commentMapper.toReplyResponseDto(comment)).thenReturn(replyResponseDto);

        ReplyResponseDto result = commentService.getCommentByParent(UUID.fromString(UUID_CONSTANT));

        assertNotNull(result);
        assertEquals(replyResponseDto, result);

        verify(commentDao).getComment(UUID.fromString(UUID_CONSTANT));
        verify(commentMapper).toReplyResponseDto(comment);
    }

    @Test
    void testGetAllParentCommentsByTopic() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Comment> comments = new PageImpl<>(Collections.singletonList(comment));

        doNothing().when(topicDataIntegrity).validateTopicId(UUID.fromString(UUID_CONSTANT));
        when(commentDao.getAllParentCommentsByTopic(UUID.fromString(UUID_CONSTANT), pageable)).thenReturn(comments);
        when(commentMapper.toParentCommentDto(comment)).thenReturn(parentCommentResponseDto);

        Page<ParentCommentResponseDto> result = commentService.getAllParentCommentsByTopic(UUID.fromString(UUID_CONSTANT), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(parentCommentResponseDto, result.getContent().get(0));

        verify(commentDao).getAllParentCommentsByTopic(UUID.fromString(UUID_CONSTANT), pageable);
        verify(commentMapper).toParentCommentDto(comment);
    }

    @Test
    void testGetAllParentCommentsByTopicWhenTopicIdIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(new InvalidCommentRequestException("Topic ID cannot be null"))
                .when(topicDataIntegrity).validateTopicId(null);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.getAllParentCommentsByTopic(null, pageable));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testGetAllRepliesByTopicWhenCommentIdIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(new InvalidCommentRequestException("Comment ID cannot be null"))
                .when(commentDataIntegrity).validateCommentId(null);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.getAllRepliesByParent(null, pageable));
        assertEquals("Comment ID cannot be null", exception.getMessage());
    }

    @Test
    void testGetAllRepliesByParent() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Comment> replies = new PageImpl<>(Collections.singletonList(comment));

        when(commentDao.getAllRepliesByParent(UUID.fromString(UUID_CONSTANT), pageable)).thenReturn(replies);
        when(commentMapper.toReplyResponseDto(comment)).thenReturn(replyResponseDto);

        Page<ReplyResponseDto> result = commentService.getAllRepliesByParent(UUID.fromString(UUID_CONSTANT), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(replyResponseDto, result.getContent().get(0));

        verify(commentDao).getAllRepliesByParent(UUID.fromString(UUID_CONSTANT), pageable);
        verify(commentMapper).toReplyResponseDto(comment);
    }

    @Test
    void testDeleteCommentWhenUserIsNotAuthorized() {
        var anotherUser = User.builder().id(UUID.randomUUID()).build();

        when(commentDao.getComment(UUID.fromString(UUID_CONSTANT))).thenReturn(comment);
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(anotherUser);

        try {
            commentService.deleteComment(UUID.fromString(UUID_CONSTANT));
        } catch (UnauthorizedAccessException e) {
            assertEquals(CommentServiceImpl.NOT_AUTHORIZED_TO_DELETE, e.getMessage());
        }

        verify(commentDao).getComment(UUID.fromString(UUID_CONSTANT));
        verify(authenticationService).getAuthenticatedAndPersistedUser();
    }

    @Test
    void testDeleteComment() {
        doNothing().when(commentDataIntegrity).validateCommentId(UUID.fromString(UUID_CONSTANT));
        when(commentDao.getComment(UUID.fromString(UUID_CONSTANT))).thenReturn(comment);
        when(commentDao.saveComment(comment)).thenReturn(comment);
        when(authenticationService.getAuthenticatedAndPersistedUser()).thenReturn(user);
        when(commentMapper.toParentCommentDto(comment)).thenReturn(parentCommentResponseDto);

        ParentCommentResponseDto result = commentService.deleteComment(UUID.fromString(UUID_CONSTANT));

        System.out.println(comment.getText());
        System.out.println(comment.isEnabled());

        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);
        assertEquals("[Deleted comment]", comment.getText());
        assertFalse(comment.isEnabled());

        verify(commentDao).getComment(UUID.fromString(UUID_CONSTANT));
        verify(commentDao).saveComment(comment);
        verify(authenticationService).getAuthenticatedAndPersistedUser();
        verify(commentMapper).toParentCommentDto(comment);
    }

    @Test
    void testMinCommentLength() {
        String shortText = "abc4";
        commentRequestDto.setText(shortText);

        doThrow(new InvalidCommentRequestException("Comment text must be at least 5 characters long"))
                .when(commentDataIntegrity).validateCommentRequestDto(commentRequestDto);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text must be at least 5 characters long", exception.getMessage());
    }

    @Test
    void testMaxCommentLength() {
        String longText = "a".repeat(601);
        commentRequestDto.setText(longText);

        doThrow(new InvalidCommentRequestException("Comment text must be at most 600 characters long"))
                .when(commentDataIntegrity).validateCommentRequestDto(commentRequestDto);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text must be at most 600 characters long", exception.getMessage());
    }

    @Test
    void testSaveIfTopicIdIsNull() {
        commentRequestDto.setTopicId(null);

        doThrow(new InvalidCommentRequestException("Topic ID cannot be null"))
                .when(commentDataIntegrity).validateCommentRequestDto(commentRequestDto);

        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testSaveIfUserIsNotAuthenticated() {
        commentRequestDto.setTopicId(UUID.fromString(UUID_CONSTANT));

        doThrow(new UnauthorizedAccessException("User is not authenticated"))
                .when(authenticationService).getAuthenticatedAndPersistedUser();

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("User is not authenticated", exception.getMessage());
    }
}
