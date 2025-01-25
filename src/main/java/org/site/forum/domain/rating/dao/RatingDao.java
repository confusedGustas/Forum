package org.site.forum.domain.rating.dao;

import org.site.forum.domain.rating.entity.Rating;
import java.util.Optional;
import java.util.UUID;

public interface RatingDao {

    Optional<Rating> findByPostIdAndUserId(UUID postId, UUID userId);
    void save(Rating rating);
    void delete(Rating rating);

}
