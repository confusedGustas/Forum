package org.site.forum.domain.rating.dao;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.repository.RatingRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingDaoImpl implements RatingDao {

    private static final String POST_ID_AND_USER_ID_MUST_NOT_BE_NULL = "PostId and UserId must not be null";
    private static final String RATING_MUST_NOT_BE_NULL = "Rating must not be null";
    private static final String RATING_SCORE_INVALID = "Rating score must be between -1, 0, 1";
    private static final String RATING_NOT_FOUND = "Rating not found";

    private final RatingRepository ratingRepository;

    @Override
    public Optional<Rating> findByPostIdAndUserId(UUID postId, UUID userId) {
        if (postId == null || userId == null) {
            throw new IllegalArgumentException(POST_ID_AND_USER_ID_MUST_NOT_BE_NULL);
        }

        return ratingRepository.findByTopicIdAndUserId(postId, userId);
    }

    @Override
    public void save(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException(RATING_MUST_NOT_BE_NULL);
        }

        if (rating.getRatingValue() < -1 || rating.getRatingValue() > 1) {
            throw new IllegalArgumentException(RATING_SCORE_INVALID);
        }

        ratingRepository.save(rating);
    }

    @Override
    public void delete(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException(RATING_MUST_NOT_BE_NULL);
        }

        Optional<Rating> existingRating = ratingRepository.findByTopicIdAndUserId(
                rating.getTopic().getId(), rating.getUser().getId());
        if (existingRating.isEmpty()) {
            throw new EntityNotFoundException(RATING_NOT_FOUND);
        }

        ratingRepository.delete(rating);
    }

}