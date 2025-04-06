package org.site.forum.domain.comment.service;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.site.forum.domain.comment.dto.response.ReplyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

public interface CommentService {
    ParentCommentResponseDto saveComment(CommentRequestDto commentRequestDto);
    ReplyResponseDto getCommentByParent(UUID parentCommentId);
    ParentCommentResponseDto deleteComment(UUID commentId);
    Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest);
    Page<ReplyResponseDto> getAllRepliesByParent(UUID parentCommentId, PageRequest pageRequest);
}