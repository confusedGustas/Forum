package org.site.forum.domain.topic.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidUserIdException;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrity;
import org.site.forum.domain.topic.repository.TopicRepository;
import org.site.forum.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.site.forum.domain.comment.dao.CommentDaoImpl.USER_WITH_THE_SPECIFIED_ID_DOES_NOT_EXIST;

@Service
@AllArgsConstructor
public class TopicDaoImpl implements TopicDao {

    private static final String TOPIC_WITH_ID_NOT_FOUND = "Topic with the specified id does not exist";
    private static final String DELETED_TOPIC_TITLE = "This topic has been deleted";
    private static final String DELETED_TOPIC_CONTENT = "This topic has been deleted";

    private final TopicRepository topicRepository;
    private final TopicDataIntegrity topicDataIntegrity;
    private final UserRepository userRepository;

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

    @Override
    public Page<Topic> getAllTopicsByUserId(UUID userId, Pageable pageable) {
        checkIfUserExists(userId);

        return topicRepository.findAllTopicsByAuthorId(userId, pageable);
    }

    private void checkIfUserExists(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new InvalidUserIdException(USER_WITH_THE_SPECIFIED_ID_DOES_NOT_EXIST);
        }
    }

    @Override
    public Topic updateTopic(UUID id, Topic topic) {
        topicDataIntegrity.validateTopicId(id);
        topicDataIntegrity.validateTopicEntity(topic);

        Topic existingTopic = getTopic(id);

        existingTopic.setTitle(topic.getTitle());
        existingTopic.setContent(topic.getContent());
        existingTopic.setUpdatedAt(LocalDateTime.now());

        return topicRepository.save(existingTopic);
    }

}