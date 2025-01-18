package org.site.forum.domain.file.repository;

import org.site.forum.domain.file.entity.File;
import org.site.forum.domain.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {

    List<File> findFilesByTopicId(UUID topicId);

    @Query("SELECT f.topic FROM File f WHERE f.id = :id")
    Topic getTopicById(UUID id);

}
