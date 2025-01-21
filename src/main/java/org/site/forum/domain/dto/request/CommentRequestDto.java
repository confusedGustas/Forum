package org.site.forum.domain.dto.request;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentRequestDto {

    private String text;

    private UUID topicId;

    @Nullable
    private UUID parentCommentId;

}
