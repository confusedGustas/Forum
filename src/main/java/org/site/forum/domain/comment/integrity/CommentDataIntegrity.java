package org.site.forum.domain.comment.integrity;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.entity.Comment;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

public interface CommentDataIntegrity {

    void validateComment(Comment comment);
    void validateCommentId(UUID commentId);
    void validateCommentRequestDto(CommentRequestDto commentRequestDto);
    int validatePage(Integer page);
    int validatePageSize(Integer pageSize);
    PageRequest createValidPageRequest(Integer page, Integer pageSize);

}
