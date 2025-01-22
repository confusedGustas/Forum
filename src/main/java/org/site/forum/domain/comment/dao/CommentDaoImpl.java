package org.site.forum.domain.comment.dao;

import lombok.AllArgsConstructor;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentDaoImpl implements CommentDao {

    private final CommentRepository commentRepository;
    private static final String COMMENT_DOES_NOT_EXIST = "Comment with the specified id does not exist";

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(UUID parentCommentId) {
        return commentRepository.findById(parentCommentId).orElseThrow(() ->
                new IllegalArgumentException(COMMENT_DOES_NOT_EXIST));
    }

    @Override
    public void deleteComment(UUID commentId) {
        commentRepository.delete(commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException(COMMENT_DOES_NOT_EXIST)));
    }

}
