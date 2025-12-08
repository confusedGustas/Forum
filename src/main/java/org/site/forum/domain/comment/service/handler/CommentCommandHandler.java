package org.site.forum.domain.comment.service.handler;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.UnauthorizedAccessException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.integrity.CommentDataIntegrity;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CommentCommandHandler {

    private static final String DELETED_COMMENT_TEXT = "[Deleted comment]";
    public static final String NOT_AUTHORIZED = "You are not authorized to delete this comment";

    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final AuthenticationService auth;
    private final CommentMapper mapper;
    private final CommentDataIntegrity integrity;

    public ParentCommentResponseDto save(CommentRequestDto dto) {
        integrity.validateCommentRequestDto(dto);

        var user = auth.getAuthenticatedAndPersistedUser();
        var topic = topicDao.getTopic(dto.getTopicId());
        var parent = dto.getParentCommentId() != null ? commentDao.getComment(dto.getParentCommentId()) : null;

        var entity = mapper.toEntity(dto, user, topic, parent);
        var saved = commentDao.saveComment(entity);

        return mapper.toParentCommentDto(saved);
    }

    public ParentCommentResponseDto delete(UUID commentId) {
        integrity.validateCommentId(commentId);

        var comment = commentDao.getComment(commentId);
        validateOwnership(comment);

        comment.setText(DELETED_COMMENT_TEXT);
        comment.setEnabled(false);

        return mapper.toParentCommentDto(commentDao.saveComment(comment));
    }

    private void validateOwnership(Comment comment) {
        integrity.validateComment(comment);

        UUID actorId = auth.getAuthenticatedAndPersistedUser().getId();
        if (!comment.getUser().getId().equals(actorId)) {
            throw new UnauthorizedAccessException(NOT_AUTHORIZED);
        }
    }
}