package org.site.forum.domain.comment.repository;

import org.site.forum.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c WHERE c.topic.id = :topicId AND c.parentComment IS NULL")
    Page<Comment> findAllParentCommentsByTopicId(UUID topicId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentCommentId")
    Page<Comment> findAllRepliesByParentCommentId(UUID parentCommentId, Pageable pageable);

    Page<Comment> findAllCommentsByUserId(UUID userId, Pageable pageable);

}
