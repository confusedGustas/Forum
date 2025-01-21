package org.site.forum.domain.comment.service;

import org.site.forum.domain.dto.request.CommentRequestDto;
import org.site.forum.domain.dto.response.CommentResponseDto;

public interface CommentService {

    CommentResponseDto saveComment(CommentRequestDto commentRequestDto);
}
