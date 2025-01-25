package org.site.forum.domain.rating.repository;

import org.site.forum.domain.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByTopicIdAndUserId(UUID postId, UUID userId);

}
