package org.site.forum.domain.rating.integrity;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingDataIntegrityImpl implements RatingDataIntegrity {

    private static final List<Integer> VALID_RATINGS = List.of(-1, 0, 1);
    private static final String INVALID_RATING_VALUE = "Rating must be one of: -1, 0, 1";
    private static final String RATING_NULL = "Rating must not be null";
    private static final String POST_USER_IDS_NULL = "PostId and UserId must not be null";
    private static final String USER_NULL = "User must not be null";
    private static final String TOPIC_NULL = "Topic must not be null";

    @Override
    public void validateRatingValue(Integer ratingValue) {
        if (!VALID_RATINGS.contains(ratingValue)) throw new IllegalArgumentException(INVALID_RATING_VALUE);
    }

    @Override
    public void validateRatingEntity(Rating rating) {
        if (rating == null) throw new IllegalArgumentException(RATING_NULL);
        if (!VALID_RATINGS.contains(rating.getRatingValue())) throw new IllegalArgumentException(INVALID_RATING_VALUE);
        if (rating.getTopic() == null) throw new IllegalArgumentException(TOPIC_NULL);
        if (rating.getUser() == null) throw new IllegalArgumentException(USER_NULL);
    }

    @Override
    public void validatePostIdAndUserId(UUID postId, UUID userId) {
        if (postId == null || userId == null) throw new IllegalArgumentException(POST_USER_IDS_NULL);
    }

    @Override
    public void validateUserExists(User user) {
        if (user == null) throw new UserNotFoundException(USER_NULL);
    }

}
