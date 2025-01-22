package org.site.forum.domain.topic.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.forum.common.exception.InvalidTopicIdException;
import org.site.forum.domain.topic.entity.Topic;
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
import static org.site.forum.constants.TestConstants.TOPIC_CONTENT;
import static org.site.forum.constants.TestConstants.TOPIC_TITLE;
import static org.site.forum.constants.TestConstants.UUID_CONSTANT;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TopicDaoImpl.class, UserDaoImpl.class})
public class TopicDaoTests {

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private UserDao userDao;

    private Topic topic;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(UUID.fromString(UUID_CONSTANT))
                .build();

        userDao.saveUser(user);
    }

    @Test
    void testSaveTopic() {
        topic = Topic.builder()
                .title(TOPIC_TITLE)
                .content(TOPIC_CONTENT)
                .author(user)
                .build();

        Topic savedTopic = topicDao.saveTopic(topic);

        assertNotNull(savedTopic.getId());
        assertNotNull(savedTopic.getTitle());
        assertNotNull(savedTopic.getContent());
        assertNotNull(savedTopic.getAuthor());
        assertEquals(topic.getTitle(), savedTopic.getTitle());
        assertEquals(topic.getContent(), savedTopic.getContent());
        assertEquals(topic.getAuthor(), savedTopic.getAuthor());
    }

    @Test
    void testGetTopic() {
        topic = Topic.builder()
                .title(TOPIC_TITLE)
                .content(TOPIC_CONTENT)
                .author(user)
                .build();

        Topic savedTopic = topicDao.saveTopic(topic);

        Topic foundTopic = topicDao.getTopic(savedTopic.getId());

        assertNotNull(foundTopic.getId());
        assertNotNull(foundTopic.getTitle());
        assertNotNull(foundTopic.getContent());
        assertNotNull(foundTopic.getAuthor());
        assertEquals(savedTopic.getId(), foundTopic.getId());
        assertEquals(savedTopic.getTitle(), foundTopic.getTitle());
        assertEquals(savedTopic.getContent(), foundTopic.getContent());
        assertEquals(savedTopic.getAuthor(), foundTopic.getAuthor());
    }

    @Test
    void testGetTopicWhenTopicNotFound() {
        Exception exception = assertThrows(InvalidTopicIdException.class, () -> topicDao.getTopic(UUID.fromString(UUID_CONSTANT)));
        assertEquals("Topic with the specified id does not exist", exception.getMessage());
    }

}
