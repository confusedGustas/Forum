package org.site.forum.domain.rating.mapper;

import lombok.AllArgsConstructor;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RatingMapper {

    private final TopicDao topicDao;

    public Rating toEntity(Topic topic, User user, Integer ratingValue) {
        return Rating.builder()
                .topic(topic)
                .user(user)
                .ratingValue(ratingValue)
                .build();
    }

}