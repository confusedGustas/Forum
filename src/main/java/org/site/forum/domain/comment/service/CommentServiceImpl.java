package org.site.forum.domain.comment.service;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.topic.dao.TopicDao;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final TopicDao topicDao;
    private final AuthenticationService authenticationService;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto saveComment(CommentRequestDto commentRequestDto) {
        var user = authenticationService.getAuthenticatedAndPersistedUser();
        var topic = topicDao.getTopic(commentRequestDto.getTopicId());

        var parentComment = commentRequestDto.getParentCommentId() != null ?
                commentDao.getComment(commentRequestDto.getParentCommentId()) : null;

        var comment = commentDao.saveComment(commentMapper.toEntity(commentRequestDto, user, topic, parentComment));

        return commentMapper.toDto(comment);
    }

    @Override
    public CommentResponseDto getComment(UUID parentCommentId) {
        var comment = commentDao.getComment(parentCommentId);

        return commentMapper.toDto(comment);
    }

}
