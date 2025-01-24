package org.site.forum.domain.comment.service;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;
import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface CommentService {

    CommentResponseDto saveComment(CommentRequestDto commentRequestDto);
    CommentResponseDto getCommentByParent(UUID parentCommentId);
    void deleteComment(UUID commentId);
    Page<ParentCommentResponseDto> getAllParentCommentsByTopic(UUID topicId, PageRequest pageRequest);
    Page<CommentResponseDto> getAllRepliesByParent(UUID parentCommentId, PageRequest pageRequest);

}
