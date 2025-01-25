package org.site.forum.domain.comment.dao;

import org.site.forum.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentDao {

    Comment saveComment(Comment comment);
    Comment getComment(UUID parentCommentId);
    Page<Comment> getAllParentCommentsByTopic(UUID topicId, Pageable pageable);
    Page<Comment> getAllRepliesByParent(UUID parentCommentId, Pageable pageable);

}
