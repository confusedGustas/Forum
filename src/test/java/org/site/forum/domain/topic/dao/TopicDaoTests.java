package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.common.exception.InvalidTopicTitleException;
import org.site.forum.domain.topic.entity.Topic;
import org.site.forum.domain.topic.integrity.TopicDataIntegrityImpl;
import org.site.forum.domain.user.dao.UserDao;
import org.site.forum.domain.user.dao.UserDaoImpl;
import org.site.forum.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TopicDaoImpl.class, UserDaoImpl.class, TopicDataIntegrityImpl.class})
class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    private Topic topic;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();
        userDao.saveUser(user);
    }

    @Test
    void testSaveTopic() {
        topic = Topic.builder()
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .build();
        var savedTopic = topicDao.saveTopic(topic);
        assertNotNull(savedTopic.getId());
        assertEquals("Test Title", savedTopic.getTitle());
        assertEquals("Test Content", savedTopic.getContent());
        assertEquals(user, savedTopic.getAuthor());
    }

    @Test
    void testGetTopic() {
        topic = Topic.builder()
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .build();
        var savedTopic = topicDao.saveTopic(topic);
        var foundTopic = topicDao.getTopic(savedTopic.getId());
        assertEquals(savedTopic.getId(), foundTopic.getId());
        assertEquals(savedTopic.getTitle(), foundTopic.getTitle());
        assertEquals(savedTopic.getContent(), foundTopic.getContent());
        assertEquals(savedTopic.getAuthor(), foundTopic.getAuthor());
    }

    @Test
    void testGetTopicWhenTopicNotFound() {
        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicDao.getTopic(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
        assertEquals("Topic with the specified id does not exist", exception.getMessage());
    }

    @Test
    void testDeleteTopic() {
        topic = Topic.builder()
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .build();
        var savedTopic = topicDao.saveTopic(topic);
        topicDao.deleteTopic(savedTopic.getId());
        var deletedTopic = topicDao.getTopic(savedTopic.getId());
        assertEquals("This topic has been deleted", deletedTopic.getTitle());
        assertEquals("This topic has been deleted", deletedTopic.getContent());
        assertNotNull(deletedTopic.getDeletedAt());
    }

    @Test
    void testSaveTopicWithInvalidData() {
        Topic invalidTopic = Topic.builder()
                .title("")
                .content("Test Content")
                .author(user)
                .build();
        assertThrows(InvalidTopicTitleException.class, () -> topicDao.saveTopic(invalidTopic));
    }

}