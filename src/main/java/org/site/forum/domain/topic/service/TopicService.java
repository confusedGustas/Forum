package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

}
