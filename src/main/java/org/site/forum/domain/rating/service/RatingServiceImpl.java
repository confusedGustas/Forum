package org.site.forum.domain.rating.service;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.rating.dao.RatingDao;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.mapper.RatingMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;
import java.util.UUID;

import static org.site.forum.common.constant.RatingConstant.VALID_RATINGS;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private static final String INVALID_RATING_MESSAGE = "Rating must be one of: -1, 0, 1";

    private final AuthenticationService authenticationService;
    private final RatingDao ratingDao;
    private final TopicDao topicDao;
    private final RatingMapper ratingMapper;

    @Override
    public void rateTopic(UUID topicId, Integer ratingValue) {
        validateRating(ratingValue);
        User user = authenticationService.getAuthenticatedUser();
        Rating rating = ratingDao.findByPostIdAndUserId(topicId, user.getId()).orElse(null);

        if (rating != null) {
            if (rating.getRatingValue() == ratingValue || ratingValue == 0) {
                removeRating(rating);
            } else {
                updateRating(rating, ratingValue);
            }
        } else if (ratingValue != 0) {
            createRating(topicId, user, ratingValue);
        }
    }

    private void createRating(UUID topicId, User user, Integer ratingValue) {
        Rating newRating = ratingMapper.toEntity(topicDao.getTopic(topicId), user, ratingValue);
        ratingDao.save(newRating);
        adjustTopicRating(topicId, ratingValue);
    }

    private void updateRating(Rating rating, Integer newRatingValue) {
        int difference = newRatingValue - rating.getRatingValue();
        rating.setRatingValue(newRatingValue);
        ratingDao.save(rating);
        adjustTopicRating(rating.getTopic().getId(), difference);
    }

    private void removeRating(Rating rating) {
        ratingDao.delete(rating);
        adjustTopicRating(rating.getTopic().getId(), -rating.getRatingValue());
    }

    private void adjustTopicRating(UUID topicId, int change) {
        Topic topic = topicDao.getTopic(topicId);
        topic.setRating(topic.getRating() + change);
        topicDao.saveTopic(topic);
    }

    private void validateRating(Integer rating) {
        if (!VALID_RATINGS.contains(rating)) {
            throw new IllegalArgumentException(INVALID_RATING_MESSAGE);
        }
    }

}