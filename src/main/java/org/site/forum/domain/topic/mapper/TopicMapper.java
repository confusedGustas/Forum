package org.site.forum.domain.topic.mapper;

import org.site.forum.domain.topic.dto.TopicDto;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    public TopicDto toDto(Topic topic) {

        return TopicDto.builder()
                .title(topic.getTitle())
                .content(topic.getContent())
                .author(topic.getAuthor())
                .build();
    }
}
