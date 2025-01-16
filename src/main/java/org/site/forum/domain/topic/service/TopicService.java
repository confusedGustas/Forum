package org.site.forum.domain.topic.service;

import org.site.forum.domain.topic.dto.TopicDto;
import org.springframework.stereotype.Service;

@Service
public interface TopicService {

    TopicDto createTopic(TopicDto topicDto);

}
