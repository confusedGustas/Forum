package org.site.forum.domain.rating.service;

import java.util.UUID;

public interface RatingService {

    void rateTopic(UUID topicId, Integer rating);

}
