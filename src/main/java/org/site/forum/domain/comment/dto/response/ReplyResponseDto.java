package org.site.forum.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyResponseDto {

    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private UUID userId;
    private String userName;
    private UUID topicId;
    private UUID parentCommentId;
    private List<ReplyResponseDto> replies;

}
