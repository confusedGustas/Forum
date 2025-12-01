package org.site.forum.domain.rating.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.common.exception.UserNotFoundException;
import org.site.forum.config.auth.AuthenticationService;
import org.site.forum.domain.rating.dao.RatingDao;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.integrity.RatingDataIntegrity;
import org.site.forum.domain.rating.mapper.RatingMapper;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private RatingDao ratingDao;
    @Mock
    private TopicDao topicDao;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private RatingDataIntegrity ratingDataIntegrity;
    @InjectMocks
    private RatingServiceImpl ratingService;

    private final UUID topicId = UUID.randomUUID();
    private final User user = User.builder().id(UUID.randomUUID()).build();

    @Test
    void rateTopic_newRating_createsRatingAndUpdatesTopic() {
        Topic topic = Topic.builder().id(topicId).rating(0).build();
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.empty());
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        Rating newRating = Rating.builder().ratingValue(1).topic(topic).user(user).build();
        when(ratingMapper.toEntity(topic, user, 1)).thenReturn(newRating);

        ratingService.rateTopic(topicId, 1);

        verify(ratingDao).save(newRating);
        assertEquals(1, topic.getRating());
    }

    @Test
    void rateTopic_existingRatingUpdates_updatesRatingAndAdjustsTopic() {
        Topic topic = Topic.builder().id(topicId).rating(1).build();
        Rating existingRating = Rating.builder().ratingValue(1).topic(topic).user(user).build();
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.of(existingRating));
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        ratingService.rateTopic(topicId, -1);

        ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingDao).save(ratingCaptor.capture());
        assertEquals(-1, ratingCaptor.getValue().getRatingValue());
        assertEquals(-1, topic.getRating());
    }

    @Test
    void rateTopic_existingRatingSameValue_removesRating() {
        Topic topic = Topic.builder().id(topicId).rating(1).build();
        Rating existingRating = Rating.builder().ratingValue(1).topic(topic).user(user).build();
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.of(existingRating));
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        ratingService.rateTopic(topicId, 1);

        verify(ratingDao).delete(existingRating);
        assertEquals(0, topic.getRating());
    }

    @Test
    void rateTopic_existingRatingSetToZero_removesRating() {
        Topic topic = Topic.builder().id(topicId).rating(1).build();
        Rating existingRating = Rating.builder().ratingValue(1).topic(topic).user(user).build();
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.of(existingRating));
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        ratingService.rateTopic(topicId, 0);

        verify(ratingDao).delete(existingRating);
        assertEquals(0, topic.getRating());
    }

    @Test
    void rateTopic_zeroRatingNoExisting_doesNothing() {
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.empty());
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        ratingService.rateTopic(topicId, 0);

        verify(ratingDao, never()).save(any());
        verify(ratingDao, never()).delete(any());
        verify(topicDao, never()).saveTopic(any());
    }

    @Test
    void rateTopic_UserNotAuthenticated_throwsUserNotFoundException() {
        when(authenticationService.getAuthenticatedUser()).thenReturn(null);
        doThrow(new UserNotFoundException("User must not be null")).when(ratingDataIntegrity).validateUserExists(null);
        assertThrows(UserNotFoundException.class, () -> ratingService.rateTopic(topicId, 1));
    }

    @Test
    void rateTopic_invalidRating_throwsIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Rating must be one of: -1, 0, 1"))
                .when(ratingDataIntegrity).validateRatingValue(5);

        assertThrows(IllegalArgumentException.class,
                () -> ratingService.rateTopic(topicId, 5));
    }

    @Test
    void rateTopic_topicIsFetchedTwice_andReturnsUpdatedTopic() {
        Topic initial = Topic.builder().id(topicId).rating(0).build();
        Topic updated = Topic.builder().id(topicId).rating(1).build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId())).thenReturn(Optional.empty());
        when(topicDao.getTopic(topicId))
                .thenReturn(initial)
                .thenReturn(updated);

        Rating newRating = Rating.builder().ratingValue(1).topic(initial).user(user).build();
        when(ratingMapper.toEntity(initial, user, 1)).thenReturn(newRating);

        Topic result = ratingService.rateTopic(topicId, 1);

        assertEquals(1, result.getRating());
    }

    @Test
    void rateTopic_updatesTopicDaoSaveTopicCalledForUpdate() {
        Topic topic = Topic.builder().id(topicId).rating(2).build();
        Rating existing = Rating.builder().ratingValue(2).topic(topic).user(user).build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId()))
                .thenReturn(Optional.of(existing));

        ratingService.rateTopic(topicId, -1);

        verify(topicDao).saveTopic(topic);
    }

    @Test
    void rateTopic_changeFromNegativeToPositive_updatesDifferenceCorrectly() {
        Topic topic = Topic.builder().id(topicId).rating(-1).build();
        Rating existing = Rating.builder().ratingValue(-1).topic(topic).user(user).build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId()))
                .thenReturn(Optional.of(existing));

        ratingService.rateTopic(topicId, 1);

        verify(ratingDao).save(existing);
        assertEquals(1, existing.getRatingValue());
        assertEquals(1, topic.getRating());
    }

    @Test
    void rateTopic_validateRatingValue_isAlwaysCalled() {
        Topic topic = Topic.builder().id(topicId).rating(0).build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId()))
                .thenReturn(Optional.empty());

        Rating newRating = Rating.builder().ratingValue(1).topic(topic).user(user).build();
        when(ratingMapper.toEntity(topic, user, 1)).thenReturn(newRating);

        ratingService.rateTopic(topicId, 1);

        verify(ratingDataIntegrity).validateRatingValue(1);
    }

    @Test
    void rateTopic_updateRatingValue_isAppliedBeforeSave() {
        Topic topic = Topic.builder().id(topicId).rating(3).build();
        Rating existing = Rating.builder().ratingValue(3).topic(topic).user(user).build();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(topicDao.getTopic(topicId)).thenReturn(topic);
        when(ratingDao.findByPostIdAndUserId(topicId, user.getId()))
                .thenReturn(Optional.of(existing));

        ratingService.rateTopic(topicId, 1);

        assertEquals(1, existing.getRatingValue());
    }

}