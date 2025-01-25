package org.site.forum.domain.topic.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicDaoImpl implements TopicDao {

    private static final String TOPIC_WITH_ID_NOT_FOUND = "Topic with the specified id does not exist";
    private static final String DELETED_TOPIC_TITLE = "This topic has been deleted";
    private static final String DELETED_TOPIC_CONTENT = "This topic has been deleted";

    private final TopicRepository topicRepository;
    private final TopicDataIntegrity topicDataIntegrity;

    @Override
    public Topic saveTopic(Topic topic) {
        topicDataIntegrity.validateTopicEntity(topic);
        return topicRepository.save(topic);
    }

    @Override
    public Topic getTopic(UUID id) {
        return topicRepository.findById(id).orElseThrow(() ->
                new InvalidTopicIdException(TOPIC_WITH_ID_NOT_FOUND));
    }

    @Override
    public void deleteTopic(UUID id) {
        Topic topic = getTopic(id);
        topic.setTitle(DELETED_TOPIC_TITLE);
        topic.setContent(DELETED_TOPIC_CONTENT);
        topic.setDeletedAt(LocalDateTime.now());
        topicRepository.save(topic);
    }

}