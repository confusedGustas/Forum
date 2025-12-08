package org.site.forum.domain.comment.service.handler;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dao.CommentDao;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.integrity.CommentDataIntegrity;
import org.site.forum.domain.comment.mapper.CommentMapper;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CommentQueryHandler {

    private final CommentDao commentDao;
    private final CommentMapper mapper;
    private final CommentDataIntegrity commentIntegrity;
    private final TopicDataIntegrity topicIntegrity;

    public ReplyResponseDto getReply(UUID parentId) {
        commentIntegrity.validateCommentId(parentId);
        return mapper.toReplyResponseDto(commentDao.getComment(parentId));
    }

    public Page<ParentCommentResponseDto> getParentComments(UUID topicId, PageRequest pageRequest) {
        topicIntegrity.validateTopicId(topicId);
        return commentDao.getAllParentCommentsByTopic(topicId, pageRequest)
                .map(mapper::toParentCommentDto);
    }

    public Page<ReplyResponseDto> getReplies(UUID parentId, PageRequest pageRequest) {
        commentIntegrity.validateCommentId(parentId);
        return commentDao.getAllRepliesByParent(parentId, pageRequest)
                .map(mapper::toReplyResponseDto);
    }
}
