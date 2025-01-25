package org.site.forum.domain.comment.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dao.TopicDaoImpl;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.site.forum.constants.TestConstants.CONTENT;
import static org.site.forum.constants.TestConstants.CREATED_AT;
import static org.site.forum.constants.TestConstants.TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommentDaoImpl.class, UserDaoImpl.class, TopicDaoImpl.class})
public class CommentDaoTests {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    private Comment comment;
    private Topic topic;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .build();

        user = userDao.saveUser(user);

        topic = Topic.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(user)
                .build();

        topic = topicDao.saveTopic(topic);

        comment = Comment.builder()
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(null)
                .build();

    }

    @Test
    public void testSaveComment() {
        var savedComment = commentDao.saveComment(comment);

        assertNotNull(savedComment.getId());
        assertNotNull(savedComment.getText());
        assertNotNull(savedComment.getCreatedAt());
        assertNotNull(savedComment.getTopic());
        assertNotNull(savedComment.getUser());
        assertNull(savedComment.getParentComment());
        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getCreatedAt(), savedComment.getCreatedAt());
        assertEquals(comment.isEnabled(), savedComment.isEnabled());
        assertEquals(comment.getTopic(), savedComment.getTopic());
        assertEquals(comment.getUser(), savedComment.getUser());
        assertEquals(comment.getParentComment(), savedComment.getParentComment());
    }

    @Test
    public void testGetComment(){
        var savedComment = commentDao.saveComment(comment);

        var foundComment = commentDao.getComment(savedComment.getId());

        assertNotNull(foundComment.getId());
        assertNotNull(foundComment.getText());
        assertNotNull(foundComment.getCreatedAt());
        assertNotNull(foundComment.getTopic());
        assertNotNull(foundComment.getUser());
        assertNull(foundComment.getParentComment());
        assertEquals(savedComment.getId(), foundComment.getId());
        assertEquals(savedComment.getText(), foundComment.getText());
        assertEquals(savedComment.getCreatedAt(), foundComment.getCreatedAt());
        assertEquals(savedComment.isEnabled(), foundComment.isEnabled());
        assertEquals(savedComment.getTopic(), foundComment.getTopic());
        assertEquals(savedComment.getUser(), foundComment.getUser());
        assertEquals(savedComment.getParentComment(), foundComment.getParentComment());

    }

    @Test
    public void testGetCommentWhenCommentNotFound(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentDao.getComment(UUID.fromString(UUID_CONSTANT)));
        assertEquals("Comment with the specified id does not exist", exception.getMessage());
    }

    @Test
    public void testGetAllParentCommentsByTopic(){
        var savedComment = commentDao.saveComment(comment);

        var comments = commentDao.getAllParentCommentsByTopic(topic.getId(), PageRequest.of(0, 10));

        assertEquals(1, comments.getTotalElements());
        assertEquals(savedComment.getId(), comments.getContent().get(0).getId());
        assertEquals(savedComment.getText(), comments.getContent().get(0).getText());
        assertEquals(savedComment.getCreatedAt(), comments.getContent().get(0).getCreatedAt());
        assertEquals(savedComment.isEnabled(), comments.getContent().get(0).isEnabled());
        assertEquals(savedComment.getTopic(), comments.getContent().get(0).getTopic());
        assertEquals(savedComment.getUser(), comments.getContent().get(0).getUser());
        assertEquals(savedComment.getTopic(), comments.getContent().get(0).getTopic());
        assertEquals(savedComment.getParentComment(), comments.getContent().get(0).getParentComment());
    }

    @Test
    public void testGetAllRepliesByParent(){
        var savedParentComment = commentDao.saveComment(comment);

        var reply = Comment.builder()
                .text(CONTENT)
                .createdAt(CREATED_AT)
                .isEnabled(true)
                .user(user)
                .topic(topic)
                .parentComment(comment)
                .build();

        var savedReply = commentDao.saveComment(reply);

        var replies = commentDao.getAllRepliesByParent(savedParentComment.getId(), PageRequest.of(0, 10));

        assertEquals(1, replies.getTotalElements());
        assertEquals(savedReply.getId(), replies.getContent().get(0).getId());
        assertEquals(savedReply.getText(), replies.getContent().get(0).getText());
        assertEquals(savedReply.getCreatedAt(), replies.getContent().get(0).getCreatedAt());
        assertEquals(savedReply.isEnabled(), replies.getContent().get(0).isEnabled());
        assertEquals(savedReply.getTopic(), replies.getContent().get(0).getTopic());
        assertEquals(savedReply.getUser(), replies.getContent().get(0).getUser());
        assertEquals(savedReply.getTopic(), replies.getContent().get(0).getTopic());
        assertEquals(savedReply.getParentComment(), replies.getContent().get(0).getParentComment());

    }

}
