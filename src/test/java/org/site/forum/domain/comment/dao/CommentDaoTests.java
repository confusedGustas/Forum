package org.site.forum.domain.comment.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.InvalidCommentException;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.file.dao.FileDao;
import org.site.forum.domain.file.service.ImageModerationService;
import org.site.forum.domain.topic.dao.TopicDao;
import org.site.forum.domain.topic.dao.TopicDaoImpl;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrityImpl;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
@Import({CommentDaoImpl.class, UserDaoImpl.class, TopicDaoImpl.class, TopicDataIntegrityImpl.class, User.class, Topic.class})
class CommentDaoTests {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TopicDao topicDao;

    @MockitoBean
    private FileDao fileDao;

    @MockitoBean
    private ImageModerationService imageModerationService;

    private Comment comment;
    private Topic topic;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .name("test")
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
    void testSaveComment() {
        var savedComment = commentDao.saveComment(comment);

        assertNotNull(savedComment.getId());
        assertEquals(comment.getId(), savedComment.getId());
    }

    @Test
    void testGetComment(){
        var savedComment = commentDao.saveComment(comment);

        var foundComment = commentDao.getComment(savedComment.getId());

        assertNotNull(foundComment.getId());
        assertNull(foundComment.getParentComment());
        assertEquals(savedComment.getId(), foundComment.getId());
        assertEquals(savedComment.getParentComment(), foundComment.getParentComment());

    }

    @Test
    void testGetCommentWhenCommentNotFound(){
        Exception exception = assertThrows(InvalidCommentException.class, () -> commentDao.getComment(UUID.fromString(UUID_CONSTANT)));
        assertEquals("Comment with the specified id does not exist", exception.getMessage());
    }

    @Test
    void testGetAllParentCommentsByTopic(){
        var savedComment = commentDao.saveComment(comment);

        var comments = commentDao.getAllParentCommentsByTopic(topic.getId(), PageRequest.of(0, 10));

        assertEquals(1, comments.getTotalElements());
        assertEquals(savedComment.getId(), comments.getContent().get(0).getId());
        assertEquals(savedComment.getText(), comments.getContent().get(0).getText());
        assertEquals(savedComment.getCreatedAt(), comments.getContent().get(0).getCreatedAt());
    }

    @Test
    void testGetAllRepliesByParent(){
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

    }

    @Test
    void testGetAllParentCommentsByTopicWhenTopicDoesNotExist(){
        Exception exception = assertThrows(InvalidTopicIdException.class, () -> commentDao.getAllParentCommentsByTopic(UUID.fromString(UUID_CONSTANT), PageRequest.of(0, 10)));
        assertEquals("Topic with the specified id does not exist", exception.getMessage());
    }

    @Test
    void testGetAllRepliesByParentWhenParentCommentDoesNotExist(){
        Exception exception = assertThrows(InvalidCommentException.class, () -> commentDao.getAllRepliesByParent(UUID.fromString(UUID_CONSTANT), PageRequest.of(0, 10)));
        assertEquals("Comment with the specified id does not exist", exception.getMessage());
    }


}
