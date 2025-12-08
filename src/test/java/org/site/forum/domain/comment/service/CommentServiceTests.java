package org.site.forum.domain.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.InvalidCommentRequestException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.service.handler.CommentCommandHandler;
import org.site.forum.domain.comment.service.handler.CommentQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.site.forum.constants.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private CommentCommandHandler commandHandler;

    @Mock
    private CommentQueryHandler queryHandler;

    @InjectMocks
    private CommentServiceImpl commentService;

    private ParentCommentResponseDto parentCommentResponseDto;
    private ReplyResponseDto replyResponseDto;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
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
                .authorId(UUID.randomUUID())
                .topicId(UUID.randomUUID())
                .build();

        replyResponseDto = ReplyResponseDto.builder()
                .id(UUID.randomUUID())
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .userId(UUID.randomUUID())
                .topicId(UUID.randomUUID())
                .parentCommentId(UUID.randomUUID())
                .replies(Collections.emptyList())
                .build();
    }

    @Test
    void testSaveRootComment() {
        when(commandHandler.save(commentRequestDto)).thenReturn(parentCommentResponseDto);
        ParentCommentResponseDto result = commentService.saveComment(commentRequestDto);
        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);
        verify(commandHandler).save(commentRequestDto);
    }

    @Test
    void testSaveReply() {
        UUID parentId = UUID.randomUUID();
        commentRequestDto.setParentCommentId(parentId);
        when(commandHandler.save(commentRequestDto)).thenReturn(parentCommentResponseDto);
        ParentCommentResponseDto result = commentService.saveComment(commentRequestDto);
        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);
        verify(commandHandler).save(commentRequestDto);
    }

    @Test
    void testSaveCommentWhenTextIsNull() {
        commentRequestDto.setText(null);
        doThrow(new InvalidCommentRequestException("Comment text cannot be empty or null"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text cannot be empty or null", exception.getMessage());
    }

    @Test
    void testSaveCommentWhenTextIsEmpty() {
        commentRequestDto.setText("");
        doThrow(new InvalidCommentRequestException("Comment text cannot be empty or null"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text cannot be empty or null", exception.getMessage());
    }

    @Test
    void testGetCommentByParent() {
        when(queryHandler.getReply(UUID.fromString(UUID_CONSTANT))).thenReturn(replyResponseDto);
        ReplyResponseDto result = commentService.getCommentByParent(UUID.fromString(UUID_CONSTANT));
        assertNotNull(result);
        assertEquals(replyResponseDto, result);
        verify(queryHandler).getReply(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void testGetAllParentCommentsByTopic() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ParentCommentResponseDto> page = new PageImpl<>(Collections.singletonList(parentCommentResponseDto));
        when(queryHandler.getParentComments(UUID.fromString(UUID_CONSTANT), pageable)).thenReturn(page);
        Page<ParentCommentResponseDto> result = commentService.getAllParentCommentsByTopic(UUID.fromString(UUID_CONSTANT), pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(queryHandler).getParentComments(UUID.fromString(UUID_CONSTANT), pageable);
    }

    @Test
    void testGetAllParentCommentsByTopicWhenTopicIdIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);
        doThrow(new InvalidCommentRequestException("Topic ID cannot be null"))
                .when(queryHandler).getParentComments(null, pageable);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.getAllParentCommentsByTopic(null, pageable));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testGetAllRepliesByTopicWhenCommentIdIsNull() {
        PageRequest pageable = PageRequest.of(0, 10);
        doThrow(new InvalidCommentRequestException("Comment ID cannot be null"))
                .when(queryHandler).getReplies(null, pageable);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.getAllRepliesByParent(null, pageable));
        assertEquals("Comment ID cannot be null", exception.getMessage());
    }

    @Test
    void testGetAllRepliesByParent() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ReplyResponseDto> page = new PageImpl<>(Collections.singletonList(replyResponseDto));
        when(queryHandler.getReplies(UUID.fromString(UUID_CONSTANT), pageable)).thenReturn(page);
        Page<ReplyResponseDto> result = commentService.getAllRepliesByParent(UUID.fromString(UUID_CONSTANT), pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(queryHandler).getReplies(UUID.fromString(UUID_CONSTANT), pageable);
    }

    @Test
    void testDeleteCommentWhenUserIsNotAuthorized() {
        doThrow(new UnauthorizedAccessException(CommentCommandHandler.NOT_AUTHORIZED))
                .when(commandHandler).delete(UUID.fromString(UUID_CONSTANT));
        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> commentService.deleteComment(UUID.fromString(UUID_CONSTANT)));
        assertEquals(CommentCommandHandler.NOT_AUTHORIZED, exception.getMessage());
        verify(commandHandler).delete(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void testDeleteComment() {
        when(commandHandler.delete(UUID.fromString(UUID_CONSTANT))).thenReturn(parentCommentResponseDto);
        ParentCommentResponseDto result = commentService.deleteComment(UUID.fromString(UUID_CONSTANT));
        assertNotNull(result);
        assertEquals(parentCommentResponseDto, result);
        verify(commandHandler).delete(UUID.fromString(UUID_CONSTANT));
    }

    @Test
    void testMinCommentLength() {
        commentRequestDto.setText("abc4");
        doThrow(new InvalidCommentRequestException("Comment text must be at least 5 characters long"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text must be at least 5 characters long", exception.getMessage());
    }

    @Test
    void testMaxCommentLength() {
        commentRequestDto.setText("a".repeat(601));
        doThrow(new InvalidCommentRequestException("Comment text must be at most 600 characters long"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Comment text must be at most 600 characters long", exception.getMessage());
    }

    @Test
    void testSaveIfTopicIdIsNull() {
        commentRequestDto.setTopicId(null);
        doThrow(new InvalidCommentRequestException("Topic ID cannot be null"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(InvalidCommentRequestException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("Topic ID cannot be null", exception.getMessage());
    }

    @Test
    void testSaveIfUserIsNotAuthenticated() {
        doThrow(new UnauthorizedAccessException("User is not authenticated"))
                .when(commandHandler).save(commentRequestDto);
        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> commentService.saveComment(commentRequestDto));
        assertEquals("User is not authenticated", exception.getMessage());
    }
}
