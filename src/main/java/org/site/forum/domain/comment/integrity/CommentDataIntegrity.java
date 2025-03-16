package org.site.forum.domain.comment.integrity;

import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.entity.Comment;

import java.util.UUID;

public interface CommentDataIntegrity {

    void validateComment(Comment comment);
    void validateCommentId(UUID commentId);
    void validateCommentRequestDto(CommentRequestDto commentRequestDto);

}
