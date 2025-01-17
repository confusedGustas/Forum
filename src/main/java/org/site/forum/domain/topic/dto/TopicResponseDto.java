package org.site.forum.domain.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.user.entity.User;

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
