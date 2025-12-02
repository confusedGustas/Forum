package org.site.forum.domain.rating.service;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.rating.dao.RatingDao;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.integrity.RatingDataIntegrity;
import org.site.forum.domain.rating.mapper.RatingMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final AuthenticationService authenticationService;
    private final RatingDao ratingDao;
    private final TopicDao topicDao;
    private final RatingMapper ratingMapper;
    private final RatingDataIntegrity ratingDataIntegrity;

    @Override
    public Topic rateTopic(UUID topicId, Integer ratingValue) {
        validateRequest(ratingValue);

        User user = authenticationService.getAuthenticatedUser();
        Topic topic = topicDao.getTopic(topicId);

        Rating existing = ratingDao.findByPostIdAndUserId(topicId, user.getId()).orElse(null);

        processRating(topic, user, existing, ratingValue);

        return topicDao.getTopic(topicId);
    }

    private void validateRequest(int ratingValue) {
        ratingDataIntegrity.validateRatingValue(ratingValue);
        ratingDataIntegrity.validateUserExists(authenticationService.getAuthenticatedUser());
    }

    private void processRating(Topic topic, User user, Rating existing, int newValue) {
        if (existing == null) {
            createIfNonZero(topic, user, newValue);
            return;
        }

        if (shouldRemove(existing, newValue)) {
            remove(existing);
            return;
        }

        update(existing, newValue);
    }

    private boolean shouldRemove(Rating rating, int newValue) {
        return newValue == 0 || rating.getRatingValue() == newValue;
    }

    private void createIfNonZero(Topic topic, User user, int newValue) {
        if (newValue == 0) return;
        Rating rating = ratingMapper.toEntity(topic, user, newValue);
        ratingDao.save(rating);
        adjust(topic, newValue);
    }

    private void update(Rating rating, int newValue) {
        int difference = newValue - rating.getRatingValue();
        rating.setRatingValue(newValue);
        ratingDao.save(rating);
        adjust(rating.getTopic(), difference);
    }

    private void remove(Rating rating) {
        int old = rating.getRatingValue();
        ratingDao.delete(rating);
        adjust(rating.getTopic(), -old);
    }

    private void adjust(Topic topic, int change) {
        topic.setRating(topic.getRating() + change);
        topicDao.saveTopic(topic);
    }
}