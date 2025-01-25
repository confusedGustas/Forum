package org.site.forum.domain.comment.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidCommentIdException;
import org.site.forum.common.exception.InvalidCommentRequestException;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final String DELETED_COMMENT_TEXT = "[Deleted comment]";
    public static final String NOT_AUTHORIZED_TO_DELETE = "You are not authorized to delete this comment";
    private static final String COMMENT_ID_CANNOT_BE_NULL = "Specified ID cannot be null";
    public static final String COMMENT_REQUEST_DATA_CANNOT_BE_NULL = "Comment request data cannot be null";
    public static final String COMMENT_TEXT_CANNOT_BE_EMPTY_OR_NULL = "Comment text cannot be empty or null";
    public static final String TOPIC_ID_CANNOT_BE_NULL = "Topic ID cannot be null";

    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final AuthenticationService authenticationService;
    private final CommentMapper commentMapper;

    @Override
    public ParentCommentResponseDto saveComment(CommentRequestDto commentRequestDto) {
        validateCommentRequestDto(commentRequestDto);

        var user = authenticationService.getAuthenticatedAndPersistedUser();
        var topic = topicDao.getTopic(commentRequestDto.getTopicId());

        var parentComment = commentRequestDto.getParentCommentId() != null ?
                commentDao.getComment(commentRequestDto.getParentCommentId()) : null;

        var comment = commentDao.saveComment(commentMapper.toEntity(commentRequestDto, user, topic, parentComment));

        return commentMapper.toParentCommentDto(comment);
    }

    @Override
    public ReplyResponseDto getCommentByParent(UUID parentCommentId) {
        var comment = commentDao.getComment(parentCommentId);

        return commentMapper.toReplyResponseDto(comment);
    }

    @Override
    public ParentCommentResponseDto deleteComment(UUID commentId) {
        checkCommentId(commentId);

        var comment = commentDao.getComment(commentId);
        checkAuthorization(comment);

        comment.setText(DELETED_COMMENT_TEXT);
        comment.setEnabled(false);

        return commentMapper.toParentCommentDto(commentDao.saveComment(comment));
    }

    @Override
    public Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest) {
        if(topicId == null) {
            throw new InvalidCommentRequestException(TOPIC_ID_CANNOT_BE_NULL);
        }

        var comments = commentDao.getAllParentCommentsByTopic(topicId, pageRequest);

        return comments.map(commentMapper::toParentCommentDto);
    }

    @Override
    public Page<ReplyResponseDto> getAllRepliesByParent(UUID parentCommentId, PageRequest pageRequest) {
        checkCommentId(parentCommentId);

        var replies = commentDao.getAllRepliesByParent(parentCommentId, pageRequest);

        return replies.map(commentMapper::toReplyResponseDto);
    }

    private void checkAuthorization(Comment comment) {
        if(comment == null) {
            throw new InvalidCommentIdException("Comment not found");
        }

        if (!comment.getUser().getId().equals(authenticationService.getAuthenticatedAndPersistedUser().getId())) {
            throw new UnauthorizedAccessException(NOT_AUTHORIZED_TO_DELETE);
        }
    }

    private void checkCommentId(UUID id) {
        if (id == null) {
            throw new InvalidCommentIdException(COMMENT_ID_CANNOT_BE_NULL);
        }
    }

    private void validateCommentRequestDto(CommentRequestDto commentRequestDto) {
        if (commentRequestDto == null) {
            throw new InvalidCommentRequestException(COMMENT_REQUEST_DATA_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(commentRequestDto.getText())) {
            throw new InvalidCommentRequestException(COMMENT_TEXT_CANNOT_BE_EMPTY_OR_NULL);
        }

        if (commentRequestDto.getTopicId() == null) {
            throw new InvalidCommentRequestException(TOPIC_ID_CANNOT_BE_NULL);
        }
    }

}
