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
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicDaoImpl implements TopicDao {

    private static final String TOPIC_WITH_ID_NOT_FOUND = "Topic with the specified id does not exist";
    private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
    private static final String TOPIC_TITLE_CANNOT_BE_EMPTY_OR_NULL = "Topic title cannot be empty or null";
    private static final String TOPIC_CONTENT_CANNOT_BE_EMPTY_OR_NULL = "Topic content cannot be empty or null";
    private static final String TOPIC_AUTHOR_CANNOT_BE_NULL = "Topic author cannot be null";
    private static final String DELETED_TOPIC_TITLE = "This topic has been deleted";
    private static final String DELETED_TOPIC_CONTENT = "This topic has been deleted";

    private final TopicRepository topicRepository;

    @Override
    public Topic saveTopic(Topic topic) {
        validateTopic(topic);
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

    private void validateTopic(Topic topic) {
        if (topic == null) {
            throw new InvalidTopicException(TOPIC_CANNOT_BE_NULL);
        }

        if (!StringUtils.hasText(topic.getTitle())) {
            throw new InvalidTopicTitleException(TOPIC_TITLE_CANNOT_BE_EMPTY_OR_NULL);
        }

        if (!StringUtils.hasText(topic.getContent())) {
            throw new InvalidTopicContentException(TOPIC_CONTENT_CANNOT_BE_EMPTY_OR_NULL);
        }

        if (topic.getAuthor() == null) {
            throw new InvalidTopicAuthorException(TOPIC_AUTHOR_CANNOT_BE_NULL);
        }
    }

}