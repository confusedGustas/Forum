package org.site.forum.domain.comment.integrity;

import org.site.forum.common.exception.InvalidCommentException;
import org.site.forum.common.exception.InvalidCommentIdException;
import org.site.forum.common.exception.InvalidCommentRequestException;
import org.site.forum.domain.comment.dto.request.CommentRequestDto;
import org.site.forum.domain.comment.entity.Comment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CommentDataIntegrityImpl implements CommentDataIntegrity {

    public static final String COMMENT_REQUEST_DATA_CANNOT_BE_NULL = "Comment request data cannot be null";
    public static final String COMMENT_TEXT_CANNOT_BE_EMPTY_OR_NULL = "Comment text cannot be empty or null";
    public static final String COMMENT_CANNOT_BE_NULL = "Comment cannot be null";
    public static final String COMMENT_ID_CANNOT_BE_NULL = "Comment ID cannot be null";
    public static final String TOPIC_ID_CANNOT_BE_NULL = "Topic ID cannot be null";

    @Override
    public void validateComment(Comment comment) {
        if(comment == null) {
            throw new InvalidCommentException(COMMENT_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateCommentId(UUID commentId) {
        if(commentId == null) {
            throw new InvalidCommentIdException(COMMENT_ID_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateCommentRequestDto(CommentRequestDto commentRequestDto) {
        if (commentRequestDto == null) {
            throw new InvalidCommentRequestException(COMMENT_REQUEST_DATA_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(commentRequestDto.getText())) {
            throw new InvalidCommentRequestException(COMMENT_TEXT_CANNOT_BE_EMPTY_OR_NULL);
        }

        if (commentRequestDto.getTopicId() == null) {
            throw new InvalidCommentRequestException(TOPIC_ID_CANNOT_BE_NULL);
        }
    }

}
