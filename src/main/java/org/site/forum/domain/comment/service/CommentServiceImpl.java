package org.site.forum.domain.comment.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.site.forum.domain.comment.service.handler.CommentCommandHandler;
import org.site.forum.domain.comment.service.handler.CommentQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentCommandHandler commandHandler;
    private final CommentQueryHandler queryHandler;

    @Override
    public ParentCommentResponseDto saveComment(CommentRequestDto dto) {
        return commandHandler.save(dto);
    }

    @Override
    public ReplyResponseDto getCommentByParent(UUID id) {
        return queryHandler.getReply(id);
    }

    @Override
    public ParentCommentResponseDto deleteComment(UUID id) {
        return commandHandler.delete(id);
    }

    @Override
    public Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest) {
        return queryHandler.getParentComments(topicId, pageRequest);
    }

    @Override
    public Page<ReplyResponseDto> getAllRepliesByParent(UUID parentId, PageRequest pageRequest) {
        return queryHandler.getReplies(parentId, pageRequest);
    }
}
