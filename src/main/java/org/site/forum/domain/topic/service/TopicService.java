package org.site.forum.domain.topic.service;

import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;

public interface TopicService {

    TopicResponseDto createTopic(TopicRequestDto topicRequestDto);
    TopicResponseDto getTopic(Long id);

}
