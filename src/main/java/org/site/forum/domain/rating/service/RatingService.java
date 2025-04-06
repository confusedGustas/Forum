package org.site.forum.domain.rating.service;

import org.site.forum.domain.topic.entity.Topic;
import java.util.UUID;

public interface RatingService {

    Topic rateTopic(UUID topicId, Integer rating);

}
