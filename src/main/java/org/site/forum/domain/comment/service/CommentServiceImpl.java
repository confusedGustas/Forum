package org.site.forum.domain.comment.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final String DELETED_COMMENT_TEXT = "[Deleted comment]";
    public static final String NOT_AUTHORIZED_TO_DELETE = "You are not authorized to delete this comment";

    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final AuthenticationService authenticationService;
    private final CommentMapper commentMapper;
    private final CommentDataIntegrity commentDataIntegrity;
    private final TopicDataIntegrity topicDataIntegrity;

    @Override
    public ParentCommentResponseDto saveComment(CommentRequestDto commentRequestDto) {
        commentDataIntegrity.validateCommentRequestDto(commentRequestDto);

        var user = authenticationService.getAuthenticatedAndPersistedUser();
        var topic = topicDao.getTopic(commentRequestDto.getTopicId());

        var parentComment = commentRequestDto.getParentCommentId() != null ?
                commentDao.getComment(commentRequestDto.getParentCommentId()) : null;

        var comment = commentDao.saveComment(commentMapper.toEntity(commentRequestDto, user, topic, parentComment));

        return commentMapper.toParentCommentDto(comment);
    }

    @Override
    public ReplyResponseDto getCommentByParent(UUID parentCommentId) {
        commentDataIntegrity.validateCommentId(parentCommentId);

        var comment = commentDao.getComment(parentCommentId);

        return commentMapper.toReplyResponseDto(comment);
    }

    @Override
    public ParentCommentResponseDto deleteComment(UUID commentId) {
        commentDataIntegrity.validateCommentId(commentId);

        var comment = commentDao.getComment(commentId);
        checkAuthorization(comment);

        comment.setText(DELETED_COMMENT_TEXT);
        comment.setEnabled(false);

        return commentMapper.toParentCommentDto(commentDao.saveComment(comment));
    }

    @Override
    public Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest) {
        topicDataIntegrity.validateTopicId(topicId);

        var comments = commentDao.getAllParentCommentsByTopic(topicId, pageRequest);

        return comments.map(commentMapper::toParentCommentDto);
    }

    @Override
    public Page<ReplyResponseDto> getAllRepliesByParent(UUID parentCommentId, PageRequest pageRequest) {
        commentDataIntegrity.validateCommentId(parentCommentId);

        var replies = commentDao.getAllRepliesByParent(parentCommentId, pageRequest);

        return replies.map(commentMapper::toReplyResponseDto);
    }

    private void checkAuthorization(Comment comment) {
        commentDataIntegrity.validateComment(comment);

        if (!comment.getUser().getId().equals(authenticationService.getAuthenticatedAndPersistedUser().getId())) {
            throw new UnauthorizedAccessException(NOT_AUTHORIZED_TO_DELETE);
        }
    }

}
