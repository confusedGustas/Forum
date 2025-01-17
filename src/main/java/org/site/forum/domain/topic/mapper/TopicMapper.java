package org.site.forum.domain.topic.mapper;

import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    public TopicResponseDto toDto(Topic topic) {

        return TopicResponseDto.builder()
                .title(topic.getTitle())
                .content(topic.getContent())
                .author(topic.getAuthor())
                .build();
    }

    public Topic topicBuilder(TopicRequestDto topicRequestDto, User user) {
        return Topic.builder()
                .title(topicRequestDto.getTitle())
                .content(topicRequestDto.getContent())
                .author(user)
                .build();
    }
}
