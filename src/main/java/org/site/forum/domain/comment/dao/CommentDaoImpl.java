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

    @Override
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Comment getComment(UUID parentCommentId) {
        return commentRepository.findById(parentCommentId).orElseThrow(() ->
                new IllegalArgumentException("Comment with the specified id does not exist"));
    }
}
