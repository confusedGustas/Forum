package org.site.forum.domain.topic.dao;

import org.site.forum.domain.topic.entity.Topic;
import java.util.UUID;

public interface TopicDao {

    Topic saveTopic(Topic topic);
    Topic getTopic(UUID id);
    void deleteTopic(UUID id);
    Topic updateTopic(UUID id, Topic topic);

}
