package org.site.forum.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private User author;
    private Topic topic;
    private Comment parentComment;

}
