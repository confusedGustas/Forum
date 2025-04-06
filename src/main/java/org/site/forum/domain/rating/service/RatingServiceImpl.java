package org.site.forum.domain.rating.service;

import lombok.AllArgsConstructor;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.rating.dao.RatingDao;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.integrity.RatingDataIntegrity;
import org.site.forum.domain.rating.mapper.RatingMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dto.response.TopicResponseDto;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.mapper.TopicMapper;
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
    private final TopicMapper topicMapper;

    @Override
    public Topic rateTopic(UUID topicId, Integer ratingValue) {
        ratingDataIntegrity.validateRatingValue(ratingValue);
        User user = authenticationService.getAuthenticatedUser();
        ratingDataIntegrity.validateUserExists(user);

        Rating rating = ratingDao.findByPostIdAndUserId(topicId, user.getId()).orElse(null);

        if (rating != null) {
            handleExistingRating(rating, ratingValue);
        } else if (ratingValue != 0) {
            createNewRating(topicId, user, ratingValue);
        }

        return topicDao.getTopic(topicId);
    }

    private void handleExistingRating(Rating rating, Integer newValue) {
        if (rating.getRatingValue() == newValue || newValue == 0) {
            removeRating(rating);
        } else {
            updateRating(rating, newValue);
        }
    }

    private void createNewRating(UUID topicId, User user, Integer value) {
        Rating newRating = ratingMapper.toEntity(topicDao.getTopic(topicId), user, value);
        ratingDao.save(newRating);
        adjustTopicRating(topicId, value);
    }

    private void updateRating(Rating rating, Integer newValue) {
        int difference = newValue - rating.getRatingValue();
        rating.setRatingValue(newValue);
        ratingDao.save(rating);
        adjustTopicRating(rating.getTopic().getId(), difference);
    }

    private void removeRating(Rating rating) {
        int currentValue = rating.getRatingValue();
        ratingDao.delete(rating);
        adjustTopicRating(rating.getTopic().getId(), -currentValue);
    }

    private void adjustTopicRating(UUID topicId, int change) {
        Topic topic = topicDao.getTopic(topicId);
        topic.setRating(topic.getRating() + change);
        topicDao.saveTopic(topic);
    }

}