package org.site.forum.domain.topic.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicAuthorException;
import org.site.forum.common.exception.InvalidTopicContentException;
import org.site.forum.common.exception.InvalidTopicException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicTitleException;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicDaoImpl implements TopicDao {

    private final TopicRepository topicRepository;

    @Override
    public Topic saveTopic(Topic topic) {
        validateTopic(topic);
        return topicRepository.save(topic);
    }

    @Override
    public Topic getTopic(UUID id) {
        return topicRepository.findById(id).orElseThrow(() ->
                new InvalidTopicIdException("Topic with the specified id does not exist"));
    }

    private void validateTopic(Topic topic) {
        if (topic == null) {
            throw new InvalidTopicException("Topic cannot be null");
        }

        if (!StringUtils.hasText(topic.getTitle())) {
            throw new InvalidTopicTitleException("Topic title cannot be empty or null");
        }

        if (!StringUtils.hasText(topic.getContent())) {
            throw new InvalidTopicContentException("Topic content cannot be empty or null");
        }

        if (topic.getAuthor() == null) {
            throw new InvalidTopicAuthorException("Topic author cannot be null");
        }
    }
}