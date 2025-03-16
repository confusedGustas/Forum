package org.site.forum.domain.topic.dao;

import org.site.forum.domain.topic.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TopicDao {

    Topic saveTopic(Topic topic);
    Topic getTopic(UUID id);
    void deleteTopic(UUID id);
    Page<Topic> getAllTopicsByUserId(UUID userId, Pageable pageable);
    Topic updateTopic(UUID id, Topic topic);

}
