package org.site.forum.domain.topic.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicRequestDto {

    private String title;

    private String content;
}
