package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.dto.TopicDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    @Override
    public TopicDto createTopic(TopicDto topicDto) {

        var topic = Topic.builder()
                .title(topicDto.getTitle())
                .content(topicDto.getContent())
                .author(topicDto.getAuthor())
                .build();

        topicRepository.save(topic);

        return topicMapper.toDto(topic);
    }

}
