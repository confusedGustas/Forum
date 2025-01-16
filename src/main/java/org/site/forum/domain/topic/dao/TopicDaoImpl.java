package org.site.forum.domain.topic.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TopicDaoImpl implements TopicDao {

    private final TopicRepository topicRepository;

    @Override
    public Topic saveTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public Topic getTopic(Long id) {
        return topicRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Topic with the specified id does not exist"));
    }
}
