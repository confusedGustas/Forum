package org.site.forum.domain.user.service;

import org.site.forum.domain.comment.dto.response.ParentCommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;
public interface UserCommentService {

    Page<ParentCommentResponseDto> getCommentsByUserId(UUID userId, PageRequest pageRequest);

}
