package org.site.forum.domain.rating.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.rating.entity.Rating;
import org.site.forum.domain.rating.integrity.RatingDataIntegrity;
import org.site.forum.domain.rating.repository.RatingRepository;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingDaoTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingDataIntegrity ratingDataIntegrity;
    @InjectMocks
    private RatingDaoImpl ratingDao;

    private final UUID topicId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Topic topic = Topic.builder().id(topicId).build();
    private final User user = User.builder().id(userId).build();
    private final Rating rating = Rating.builder()
            .id(UUID.randomUUID())
            .topic(topic)
            .user(user)
            .ratingValue(1)
            .build();

    @Test
    void findByPostIdAndUserId_CallsIntegrityAndRepository() {
        when(ratingRepository.findByTopicIdAndUserId(topicId, userId)).thenReturn(Optional.of(rating));

        Optional<Rating> result = ratingDao.findByPostIdAndUserId(topicId, userId);

        assertTrue(result.isPresent());
        verify(ratingDataIntegrity).validatePostIdAndUserId(topicId, userId);
        verify(ratingRepository).findByTopicIdAndUserId(topicId, userId);
    }

    @Test
    void save_ValidatesAndPersistsRating() {
        ratingDao.save(rating);

        verify(ratingDataIntegrity).validateRatingEntity(rating);
        verify(ratingRepository).save(rating);
    }

    @Test
    void delete_ExistingRating_RemovesFromDatabase() {
        when(ratingRepository.findByTopicIdAndUserId(topicId, userId)).thenReturn(Optional.of(rating));

        ratingDao.delete(rating);

        verify(ratingDataIntegrity).validateRatingEntity(rating);
        verify(ratingRepository).delete(rating);
    }

    @Test
    void delete_NonExistentRating_ThrowsException() {
        when(ratingRepository.findByTopicIdAndUserId(topicId, userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ratingDao.delete(rating));
        verify(ratingRepository, never()).delete(rating);
    }

    @Test
    void validateRatingExists_WhenPresent_DoesNotThrow() {
        when(ratingRepository.findByTopicIdAndUserId(topicId, userId)).thenReturn(Optional.of(rating));

        assertDoesNotThrow(() -> ratingDao.validateRatingExists(topicId, userId));
    }

    @Test
    void validateRatingExists_WhenMissing_ThrowsException() {
        when(ratingRepository.findByTopicIdAndUserId(topicId, userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ratingDao.validateRatingExists(topicId, userId));
    }
}