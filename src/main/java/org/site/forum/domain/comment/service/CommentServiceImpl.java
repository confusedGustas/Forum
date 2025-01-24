package org.site.forum.domain.comment.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final AuthenticationService authenticationService;
    private final CommentMapper commentMapper;

    private static final String DELETED_COMMENT_TEXT = "[Deleted comment]";
    public static final String NOT_AUTHORIZED_TO_DELETE = "You are not authorized to delete this comment";

    @Override
    public CommentResponseDto saveComment(CommentRequestDto commentRequestDto) {
        var user = authenticationService.getAuthenticatedAndPersistedUser();
        var topic = topicDao.getTopic(commentRequestDto.getTopicId());

        var parentComment = commentRequestDto.getParentCommentId() != null ?
                commentDao.getComment(commentRequestDto.getParentCommentId()) : null;

        var comment = commentDao.saveComment(commentMapper.toEntity(commentRequestDto, user, topic, parentComment));

        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public CommentResponseDto getCommentByParent(UUID parentCommentId) {
        var comment = commentDao.getComment(parentCommentId);

        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public CommentResponseDto deleteComment(UUID commentId) {
        var comment = commentDao.getComment(commentId);
        checkAuthorization(comment);

        comment.setText(DELETED_COMMENT_TEXT);
        comment.setEnabled(false);

        return commentMapper.toCommentResponseDto(commentDao.saveComment(comment));
    }

    @Override
    public Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest) {
        var comments = commentDao.getAllParentCommentsByTopic(topicId, pageRequest);

        return comments.map(commentMapper::toParentCommentDto);
    }

    @Override
    public Page<CommentResponseDto> getAllRepliesByParent(UUID parentCommentId, PageRequest pageRequest) {
        var replies = commentDao.getAllRepliesByParent(parentCommentId, pageRequest);

        return replies.map(commentMapper::toCommentResponseDto);
    }

    private void checkAuthorization(Comment comment) {
        if (!comment.getUser().getId().equals(authenticationService.getAuthenticatedAndPersistedUser().getId())) {
            throw new IllegalArgumentException(NOT_AUTHORIZED_TO_DELETE);
        }
    }


}
