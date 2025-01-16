package org.site.forum.domain.topic.dto;

import lombok.*;
import org.site.forum.domain.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponseDto {

    private String title;

    private String content;

    private User author;
}
