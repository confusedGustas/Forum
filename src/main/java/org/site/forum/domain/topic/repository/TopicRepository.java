package org.site.forum.domain.topic.repository;

import org.site.forum.domain.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID>, JpaSpecificationExecutor<Topic> { }
