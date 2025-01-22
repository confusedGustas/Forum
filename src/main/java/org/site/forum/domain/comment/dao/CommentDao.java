package org.site.forum.domain.comment.dao;

import org.site.forum.domain.comment.entity.Comment;

import java.util.UUID;

public interface CommentDao {

    Comment saveComment(Comment comment);
    Comment getComment(UUID parentCommentId);
    void deleteComment(UUID commentId);
}
