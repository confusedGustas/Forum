package org.site.forum.domain.rating.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.integrity.RatingDataIntegrity;
import org.site.forum.domain.rating.repository.RatingRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingDaoImpl implements RatingDao {

    private static final String RATING_NOT_FOUND = "Rating not found";

    private final RatingRepository ratingRepository;
    private final RatingDataIntegrity ratingDataIntegrity;

    @Override
    public Optional<Rating> findByPostIdAndUserId(UUID postId, UUID userId) {
        ratingDataIntegrity.validatePostIdAndUserId(postId, userId);
        return ratingRepository.findByTopicIdAndUserId(postId, userId);
    }

    @Override
    public void save(Rating rating) {
        ratingDataIntegrity.validateRatingEntity(rating);
        ratingRepository.save(rating);
    }

    @Override
    public void delete(Rating rating) {
        ratingDataIntegrity.validateRatingEntity(rating);
        validateRatingExists(rating.getTopic().getId(), rating.getUser().getId());
        ratingRepository.delete(rating);
    }

    public void validateRatingExists(UUID topicId, UUID userId) {
        if (findByPostIdAndUserId(topicId, userId).isEmpty()) throw new IllegalArgumentException(RATING_NOT_FOUND);
    }

}