package org.site.forum.domain.rating.integrity;

import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.user.entity.User;
import java.util.UUID;

public interface RatingDataIntegrity {

    void validateRatingValue(Integer ratingValue);
    void validateRatingEntity(Rating rating);
    void validatePostIdAndUserId(UUID postId, UUID userId);
    void validateUserExists(User user);

}
