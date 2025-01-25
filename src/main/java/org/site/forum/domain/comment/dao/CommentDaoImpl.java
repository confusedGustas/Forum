package org.site.forum.domain.comment.dao;

import lombok.AllArgsConstructor;
import org.site.forum.common.exception.InvalidCommentException;
import org.site.forum.common.exception.InvalidTopicException;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.comment.repository.CommentRepository;
import org.site.forum.domain.topic.dao.TopicDaoImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentDaoImpl implements CommentDao {

    private final CommentRepository commentRepository;
    private final TopicDaoImpl topicDao;

    private static final String COMMENT_DOES_NOT_EXIST = "Comment with the specified id does not exist";
    private static final String TOPIC_DOES_NOT_EXIST = "Topic with the specified id does not exist";

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(UUID parentCommentId) {
        return commentRepository.findById(parentCommentId).orElseThrow(() ->
                new InvalidCommentException(COMMENT_DOES_NOT_EXIST));
    }

    @Override
    public Page<Comment> getAllParentCommentsByTopic(UUID topicId, Pageable pageable) {
        checkIfTopicExists(topicId);

        return commentRepository.findAllParentCommentsByTopicId(topicId, pageable);
    }

    @Override
    public Page<Comment> getAllRepliesByParent(UUID parentCommentId, Pageable pageable) {
        checkIfParentCommentExists(parentCommentId);

        return commentRepository.findAllRepliesByParentCommentId(parentCommentId, pageable);
    }

    private void checkIfTopicExists(UUID topicId) {
        if(topicDao.getTopic(topicId) == null) {
            throw new InvalidTopicException(TOPIC_DOES_NOT_EXIST);
        }
    }

    private void checkIfParentCommentExists(UUID parentCommentId) {
        if (commentRepository.findById(parentCommentId).isEmpty()) {
            throw new InvalidCommentException(COMMENT_DOES_NOT_EXIST);
        }
    }

}
