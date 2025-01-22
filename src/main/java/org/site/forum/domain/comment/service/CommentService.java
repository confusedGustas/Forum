package org.site.forum.domain.comment.service;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.dto.response.CommentResponseDto;

import java.util.UUID;

public interface CommentService {

    CommentResponseDto saveComment(CommentRequestDto commentRequestDto);
    CommentResponseDto getComment(UUID parentCommentId);

}
