package org.site.forum.domain.topic.service;

import lombok.AllArgsConstructor;
import org.site.forum.domain.User;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.TopicRequestDto;
import org.site.forum.domain.topic.dto.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;
    private final TopicMapper topicMapper;

    @Override
    public TopicResponseDto createTopic(TopicRequestDto topicRequestDto) {

        var topic = Topic.builder()
                .title(topicRequestDto.getTitle())
                .content(topicRequestDto.getContent())
                .author(new User())
                .build();

        return topicMapper.toDto(topicDao.saveTopic(topic));
    }

    @Override
    public TopicResponseDto getTopic(Long id) {
        return topicMapper.toDto(topicDao.getTopic(id));
    }

}
